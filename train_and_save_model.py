import os
import librosa
import numpy as np
import joblib
import json
from sklearn.cluster import KMeans
from sklearn.manifold import TSNE
import matplotlib.pyplot as plt

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

parent_dir = "path_to_audio_files"
sub_dirs = ["classical", "jazz", "metal", "pop", "rock"]
features, labels, metrics = parse_audio_files(parent_dir, sub_dirs)

def cluster_audio_features(features, n_clusters=10):
    kmeans = KMeans(n_clusters=n_clusters, random_state=42)
    clusters = kmeans.fit_predict(features)
    return clusters, kmeans

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

def save_data_to_library(features, labels, metrics, clusters):
    data = []
    for i in range(len(features)):
        data.append({
            "features": features[i].tolist(),
            "label": labels[i],
            "metrics": metrics[i],
            "cluster": int(clusters[i])
        })
    with open("radio_library.json", "w") as f:
        json.dump(data, f)

save_data_to_library(features, labels, metrics, clusters)
joblib.dump(kmeans, "audio_clustering_model.pkl")
