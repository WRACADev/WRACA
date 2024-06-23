import numpy as np
import json
import matplotlib.pyplot as plt
from sklearn.metrics import silhouette_score, silhouette_samples
from sklearn.manifold import TSNE
import joblib

def load_data():
    with open("radio_library.json", "r") as f:
        data = json.load(f)
    features = np.array([item["features"] for item in data])
    labels = np.array([item["label"] for item in data])
    metrics = [item["metrics"] for item in data]
    clusters = np.array([item["cluster"] for item in data])
    return features, labels, metrics, clusters

def evaluate_clusters(features, clusters):
    silhouette_avg = silhouette_score(features, clusters)
    sample_silhouette_values = silhouette_samples(features, clusters)
    print(f"Silhouette Score: {silhouette_avg:.2f}")

    return silhouette_avg, sample_silhouette_values

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

def main():
    features, labels, metrics, clusters = load_data()

    silhouette_avg, sample_silhouette_values = evaluate_clusters(features, clusters)

    visualize_clusters(features, clusters)

if __name__ == "__main__":
    main()
