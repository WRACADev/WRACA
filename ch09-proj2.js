document.addEventListener("DOMContentLoaded", async function () {
    // Fetch JSON data
    const response = await fetch("paintings.json");
    const paintings = await response.json();

    // Select the containers
    const paintingList = document.querySelector("#paintings ul");
    const detailsFigure = document.querySelector("#details figure");

    // Generate thumbnail images for each painting
    paintings.forEach(painting => {
        const listItem = document.createElement("li");

        const thumbnail = document.createElement("img");
        thumbnail.src = `images/small/${painting.id}.jpg`;
        thumbnail.dataset.id = painting.id;

        listItem.appendChild(thumbnail);
        paintingList.appendChild(listItem);
    });

    // Display selected painting details on thumbnail click
    paintingList.addEventListener("click", (e) => {
        if (e.target.matches("img")) {
            const paintingId = e.target.dataset.id;
            const selectedPainting = paintings.find(p => p.id === paintingId);

            // Clear existing content in details section
            detailsFigure.innerHTML = '';

            // Create title and artist elements
            const title = document.createElement("h2");
            title.textContent = selectedPainting.title;

            const artist = document.createElement("h3");
            artist.textContent = selectedPainting.artist;

            // Append title and artist above the image
            detailsFigure.append(title, artist);

            // Create and append the full-size image
            const largeImage = document.createElement("img");
            largeImage.src = `images/large/${selectedPainting.id}.jpg`;
            largeImage.className = "full-image";
            detailsFigure.appendChild(largeImage);

            // Create and append the description
            const description = document.createElement("p");
            description.textContent = selectedPainting.features.map(feature => feature.description).join(" ");
            detailsFigure.appendChild(description);
        }
    });
});
