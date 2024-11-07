document.addEventListener("DOMContentLoaded", () => {
    // Step 3: Fetch and parse the JSON data
    fetch('paintings.json')
        .then(response => response.json())
        .then(data => {
            const paintingList = document.getElementById('painting-list');
            const full = document.getElementById('full');
            const paintingTitle = document.getElementById('painting-title');
            const paintingArtist = document.getElementById('painting-artist');
            const description = document.getElementById('description');

            // Generate thumbnail images
            data.forEach(painting => {
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
                    const selectedPainting = data.find(p => p.id === paintingId);
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
                            
                            const left = feature.x1; // Upper-left x
                            const top = feature.y1; // Upper-left y
                            const width = feature.x2 - feature.x1; // Width
                            const height = feature.y2 - feature.y1; // Height

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
        })
        .catch(error => console.error('Error loading the paintings:', error));
});
