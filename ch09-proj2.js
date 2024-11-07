document.addEventListener("DOMContentLoaded", () => {
    // Step 3: Fetch and parse the JSON data
    const paintings = JSON.parse(content); // Using the provided JSON content
    const paintingList = document.getElementById('painting-list');
    const full = document.getElementById('full');
    const paintingTitle = document.getElementById('painting-title');
    const paintingArtist = document.getElementById('painting-artist');
    const description = document.getElementById('description');

    // Generate thumbnail images
    paintings.forEach(painting => {
        const li = document.createElement('li');
        const thumbnail = document.createElement('img');
        thumbnail.src = `images/thumbnails/${painting.id}.jpg`;
        thumbnail.alt = painting.title;
        thumbnail.dataset.id = painting.id; // Store the painting id
        li.appendChild(thumbnail);
        paintingList.appendChild(li);
    });

    // Step 4: Event delegation for thumbnail clicks
    paintingList.addEventListener('click', (event) => {
        if (event.target.tagName === 'IMG') {
            const paintingId = event.target.dataset.id;
            
            // Clear previous content
            full.innerHTML = '';
            description.textContent = '';

            // Find the selected painting
            const selectedPainting = paintings.find(p => p.id === paintingId);
            if (selectedPainting) {
                // Display larger painting
                const largeImage = document.createElement('img');
                largeImage.src = `images/large/${selectedPainting.id}.jpg`;
                full.appendChild(largeImage);

                // Display title and artist
                paintingTitle.textContent = selectedPainting.title;
                paintingArtist.textContent = selectedPainting.artist;

                // Step 5: Create rectangles for features
                selectedPainting.features.forEach(feature => {
                    const box = document.createElement('div');
                    box.classList.add('box');
                    
                    const left = feature.upperLeft[0]; // Upper-left x
                    const top = feature.upperLeft[1]; // Upper-left y
                    const width = feature.lowerRight[0] - feature.upperLeft[0]; // Width
                    const height = feature.lowerRight[1] - feature.upperLeft[1]; // Height

                    // Set the CSS properties
                    box.style.position = 'absolute';
                    box.style.left = `${left}px`;
                    box.style.top = `${top}px`;
                    box.style.width = `${width}px`;
                    box.style.height = `${height}px`;

                    // Step 6: Mouseover and mouseout events
                    box.addEventListener('mouseover', () => {
                        description.textContent = feature.description;
                    });

                    box.addEventListener('mouseout', () => {
                        description.textContent = '';
                    });

                    // Append the box to the painting display
                    full.appendChild(box);
                });
            }
        }
    });
});
