import os
import librosa
import numpy as np
import joblib
import json
from sklearn.cluster import KMeans
from sklearn.manifold import TSNE
import matplotlib.pyplot as plt
import spotipy
from spotipy.oauth2 import SpotifyClientCredentials
import requests
from googleapiclient.discovery import build
from bs4 import BeautifulSoup

# Function to fetch music data from Spotify
def fetch_spotify_data(spotify_client_id, spotify_client_secret, playlist_id):
    spotify_auth_manager = SpotifyClientCredentials(client_id=spotify_client_id, client_secret=spotify_client_secret)
    spotify = spotipy.Spotify(auth_manager=spotify_auth_manager)
    
    results = spotify.playlist_tracks(playlist_id)
    tracks = results['items']
    
    features = []
    for item in tracks:
        track = item['track']
        track_id = track['id']
        audio_features = spotify.audio_features(track_id)
        if audio_features:
            features.append(audio_features[0])
    
    formatted_features = []
    for feature in features:
        if feature:
            formatted_features.append([
                feature['danceability'],
                feature['energy'],
                feature['key'],
                feature['loudness'],
                feature['mode'],
                feature['speechiness'],
                feature['acousticness'],
                feature['instrumentalness'],
                feature['liveness'],
                feature['valence'],
                feature['tempo']
            ])
    
    return formatted_features

# Function to fetch audio analysis from Apple Music
def fetch_apple_music_audio_analysis(track_id, developer_token, music_user_token):
    url = f"https://api.music.apple.com/v1/catalog/us/songs/{track_id}"
    headers = {
        'Authorization': f'Bearer {developer_token}',
        'Music-User-Token': music_user_token
    }
    response = requests.get(url, headers=headers)
    if response.status_code == 200:
        data = response.json()
        attributes = data['data'][0]['attributes']
        return [
            attributes.get('previews', [{}])[0].get('url', ''),
            attributes.get('discNumber', 0),
            attributes.get('durationInMillis', 0) / 1000.0,
            attributes.get('trackNumber', 0),
            attributes.get('releaseDate', ''),
            attributes.get('playParams', {}).get('isLibrary', False),
            attributes.get('playParams', {}).get('isExplicit', False),
            attributes.get('playParams', {}).get('isPlayable', True)
        ]
    else:
        return []

# Function to fetch music data from Apple Music
def fetch_apple_music_data(developer_token, music_user_token, playlist_id):
    url = f"https://api.music.apple.com/v1/me/library/playlists/{playlist_id}/tracks"
    headers = {
        'Authorization': f'Bearer {developer_token}',
        'Music-User-Token': music_user_token
    }
    
    response = requests.get(url, headers=headers)
    tracks = response.json()['data']
    
    features = []
    for track in tracks:
        track_id = track['id']
        audio_analysis = fetch_apple_music_audio_analysis(track_id, developer_token, music_user_token)
        features.append(audio_analysis)
    
    return features

# Function to fetch music data from Bandcamp
def fetch_bandcamp_data(artist_url):
    response = requests.get(artist_url)
    soup = BeautifulSoup(response.content, 'html.parser')
    
    tracks = []
    for track in soup.select('.track_list .track_row_view'):
        title = track.select_one('.title').get_text(strip=True)
        duration = track.select_one('.time').get_text(strip=True)
        tracks.append({
            'title': title,
            'duration': duration
        })
    
    return tracks

# Function to fetch music data from YouTube
def fetch_youtube_data(youtube_api_key, playlist_id):
    youtube = build('youtube', 'v3', developerKey=youtube_api_key)
    
    request = youtube.playlistItems().list(
        part='snippet,contentDetails',
        playlistId=playlist_id,
        maxResults=50
    )
    response = request.execute()
    
    tracks = []
    for item in response['items']:
        title = item['snippet']['title']
        video_id = item['contentDetails']['videoId']
        video_request = youtube.videos().list(
            part='contentDetails,statistics',
            id=video_id
        )
        video_response = video_request.execute()
        
        if video_response['items']:
            video_data = video_response['items'][0]
            duration = video_data['contentDetails']['duration']
            views = video_data['statistics'].get('viewCount', 0)
            
            tracks.append({
                'title': title,
                'duration': duration,
                'views': views
            })
    
    return tracks

# Existing feature extraction function
def extract_features(file_name):
    try:
        audio, sample_rate = librosa.load(file_name)
        mfccs = np.mean(librosa.feature.mfcc(y=audio, sr=sample_rate, n_mfcc=40).T, axis=0)
        chroma = np.mean(librosa.feature.chroma_stft(y=audio, sr=sample_rate).T, axis=0)
        mel = np.mean(librosa.feature.melspectrogram(y=audio, sr=sample_rate).T, axis=0)
        contrast = np.mean(librosa.feature.spectral_contrast(y=audio, sr=sample_rate).T, axis=0)
        tonnetz = np.mean(librosa.feature.tonnetz(y=librosa.effects.harmonic(audio), sr=sample_rate).T, axis=0)
        return np.hstack([mfccs, chroma, mel, contrast, tonnetz])
    except Exception as e:
        print(f"Error encountered while parsing file: {file_name}")
        return None

# Existing audio analysis function
def analyze_audio(file_name):
    audio, sample_rate = librosa.load(file_name)
    tempo, _ = librosa.beat.beat_track(y=audio, sr=sample_rate)
    spectral_centroid = np.mean(librosa.feature.spectral_centroid(y=audio, sr=sample_rate))
    spectral_bandwidth = np.mean(librosa.feature.spectral_bandwidth(y=audio, sr=sample_rate))
    rms = np.mean(librosa.feature.rms(y=audio))
    bass = np.mean(librosa.feature.spectral_bandwidth(y=audio, sr=sample_rate, freq=[20, 140]))
    mids = np.mean(librosa.feature.spectral_bandwidth(y=audio, sr=sample_rate, freq=[140, 400]))
    treble = np.mean(librosa.feature.spectral_bandwidth(y=audio, sr=sample_rate, freq=[400, 6000]))
    return {
        "BPM": tempo,
        "Spectral Centroid": spectral_centroid,
        "Spectral Bandwidth": spectral_bandwidth,
        "RMS": rms,
        "Bass": bass,
        "Mids": mids,
        "Treble": treble
    }

# Existing function to parse audio files
def parse_audio_files(parent_dir, sub_dirs, file_ext="*.mp3"):
    features, labels, metrics = [], [], []
    for label, sub_dir in enumerate(sub_dirs):
        for fn in os.listdir(os.path.join(parent_dir, sub_dir)):
            if fn.endswith(file_ext):
                file_path = os.path.join(parent_dir, sub_dir, fn)
                feature = extract_features(file_path)
                if feature is not None:
                    features.append(feature)
                    labels.append(label)
                    metrics.append(analyze_audio(file_path))
    return np.array(features), np.array(labels), metrics

# Fetch additional features from Spotify, Apple Music, Bandcamp, and YouTube
def fetch_additional_features():
    spotify_features = fetch_spotify_data('your_spotify_client_id', 'your_spotify_client_secret', 'your_spotify_playlist_id')
    apple_music_features = fetch_apple_music_data('your_apple_developer_token', 'your_music_user_token', 'your_apple_playlist_id')
    bandcamp_tracks = fetch_bandcamp_data('your_bandcamp_artist_url')
    youtube_tracks = fetch_youtube_data('your_youtube_api_key', 'your_youtube_playlist_id')
    
    # Here, we need to convert these features into a numerical format
    # For simplicity, let's assume they are already in a suitable format
    
    # Combine all features
    combined_features = np.concatenate([spotify_features, apple_music_features], axis=0)
    return combined_features

# Main script
parent_dir = "path_to_audio_files"
sub_dirs = ["classical", "jazz", "metal", "pop", "rock"]
features, labels, metrics = parse_audio_files(parent_dir, sub_dirs)

# Fetch additional features and combine with existing features
additional_features = fetch_additional_features()
features = np.concatenate([features, additional_features], axis=0)

# Function to cluster audio features
def cluster_audio_features(features, n_clusters=10):
    kmeans = KMeans(n_clusters=n_clusters, random_state=42)
    clusters = kmeans.fit_predict(features)
    return clusters, kmeans

# Function to visualize clusters
def visualize_clusters(features, clusters):
    tsne = TSNE(n_components=2, perplexity=30, n_iter=300)
    tsne_results = tsne.fit_transform(features)

    plt.figure(figsize=(16, 10))
    scatter = plt.scatter(tsne_results[:, 0], tsne_results[:, 1], c=clusters, cmap='viridis')
    plt.colorbar(scatter)
    plt.title("t-SNE visualization of audio features")
    plt.xlabel("t-SNE component 1")
    plt.ylabel("t-SNE component 2")
    plt.show()

n_clusters = 10  # Define the number of clusters you want
clusters, kmeans = cluster_audio_features(features, n_clusters)

# Visualize the clusters
visualize_clusters(features, clusters)

# Function to save data to library
def save_data_to_library(features, labels, metrics, clusters):
    data = []
    for i in range(len(features)):
        data.append({
            "features": features[i].tolist(),
            "label": labels[i] if i < len(labels) else -1,  # Handle case where additional features have no labels
            "metrics": metrics[i] if i < len(metrics) else {},
            "cluster": int(clusters[i])
        })
    with open("radio_library.json", "w") as f:
        json.dump(data, f)

save_data_to_library(features, labels, metrics, clusters)
joblib.dump(kmeans, "audio_clustering_model.pkl")
