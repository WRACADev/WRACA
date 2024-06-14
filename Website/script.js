$(document).ready(function() {
    function showSection(sectionId) {
        $(".content-section").hide();
        $("#" + sectionId).show();
    }
    
    $(".dropdown-content a").on("click", function() {
        var target = $(this).attr("href").substring(1);
        showSection(target);
    });
});

document.getElementById('analyzeButton').addEventListener('click', function() {
  const fileInput = document.getElementById('audioInput');
  if (fileInput.files.length === 0) {
    alert('Please upload an audio file.');
    return;
  }

  const formData = new FormData();
  formData.append('file', fileInput.files[0]);

  fetch('http://localhost:8000/predict', {
    method: 'POST',
    body: formData
  })
  .then(response => response.json())
  .then(data => {
    const resultDiv = document.getElementById('analysisResult');
    resultDiv.innerHTML = `
      <p>Genre: ${data.genre}</p>
      <p>BPM: ${data.metrics.BPM}</p>
      <p>Spectral Centroid: ${data.metrics["Spectral Centroid"]}</p>
      <p>Spectral Bandwidth: ${data.metrics["Spectral Bandwidth"]}</p>
      <p>RMS: ${data.metrics.RMS}</p>
      <p>Bass: ${data.metrics.Bass}</p>
      <p>Mids: ${data.metrics.Mids}</p>
      <p>Treble: ${data.metrics.Treble}</p>
    `;
  })
  .catch(error => {
    console.error('Error:', error);
  });
});

// Radio
fetch('http://localhost:8000/library')
  .then(response => response.json())
  .then(data => {
    const libraryDiv = document.getElementById('library');
    data.forEach(item => {
      const itemDiv = document.createElement('div');
      itemDiv.innerHTML = `
        <p>Features: ${item.features}</p>
        <p>Label: ${item.label}</p>
        <p>Metrics: ${JSON.stringify(item.metrics)}</p>
      `;
      libraryDiv.appendChild(itemDiv);
    });
  })
  .catch(error => {
    console.error('Error:', error);
  });

$(document).ready(function () {
  $("#entryLogo, #entryBG, #entryText, #main").show();
  $("#scrollingBG").fadeIn(1500);
  $("#bg1").delay(2000).fadeIn(2000);
  $("#scrollingBG2").delay(4000).animate({
    left: "-1500px",
  });
  $("#scrollingBG3").delay(1500).fadeIn(2000);
  $("#scrollingBG4").delay(3000).animate({
    left: "600px",
  });
  $("#entryLogo").delay(6000).animate({
    left: "780px",
  });
  $("#entryText").delay(6000).animate({
    left: "25px",
  });
});

$(document).ready(function() {
  // Hide the old backgrounds
  $('.scrollingBG, .scrollingBG2, .scrollingBG3, .scrollingBG4, .bg1').hide();

  // Initialize the new background animation
  $('#newScrollingBG').show().css({
    'background': 'linear-gradient(45deg, #ff9a9e 0%, #fad0c4 99%, #fad0c4 100%)',
    'animation': 'newBackgroundAnimation 20s linear infinite'
  });
});


$(document).ready(function () {
  // Show the main container and background elements
  $("#main").css("display", "block");
  $("#scrollingBG").css("display", "block");
  $("#scrollingBG2").css("display", "grid");
  $("#scrollingBG3").css("display", "block");
  $("#scrollingBG4").css("display", "grid");
  $("#bg1").css("display", "block");
});

$(document).ready(function () {
  $(".dropbtn").click(function () {
    $(".dropdown-content").toggle();
  });

  $(document).click(function (event) {
    if (!$(event.target).closest(".dropdown").length) {
      $(".dropdown-content").hide();
    }
  });
});
// Popup Form stuff
$(document).ready(function () {
  var popupForm = $("#popupForm");
  var openFormBtn = $("#openFormBtn");
  var closeBtn = $(".close-btn");
  var chatBox = $("#chatBox");
  var chatForm = $("#chatForm");
  var chatInput = $("#chatInput");
  var forumList = $("#forumList");
  var chatContainer = $("#chatContainer");
  var forumTitle = $("#forumTitle");

  // Sample forums data
  var forums = [
    { id: 1, title: "General Discussion" },
    { id: 2, title: "Music Talk" },
    { id: 3, title: "Event Planning" },
  ];

  var currentForumId = null;

  openFormBtn.on("click", function () {
    popupForm.show();
    loadForumList();
  });

  closeBtn.on("click", function () {
    popupForm.hide();
    chatContainer.hide();
    forumList.show();
  });

  $(window).on("click", function (event) {
    if ($(event.target).is(popupForm)) {
      popupForm.hide();
    }
  });

  chatForm.on("submit", function (event) {
    event.preventDefault();
    var message = chatInput.val().trim();
    if (message) {
      // Save the message to the server
      saveMessage(currentForumId, message);

      chatBox.append("<div>" + message + "</div>");
      chatInput.val("");
      chatBox.scrollTop(chatBox[0].scrollHeight);
    }
  });

  function loadForumList() {
    forumList.empty();
    forums.forEach(function (forum) {
      var button = $("<button></button>")
        .text(forum.title)
        .attr("data-id", forum.id)
        .on("click", function () {
          var forumId = $(this).data("id");
          loadChat(forumId);
        });
      forumList.append(button);
    });
  }

  function loadChat(forumId) {
    currentForumId = forumId;
    var forum = forums.find((f) => f.id === forumId);
    forumTitle.text(forum.title);

    // Fetch messages from the server
    fetchMessages(forumId);

    chatContainer.show();
    forumList.hide();
  }

  function fetchMessages(forumId) {
    // Replace with server-side call
    // For now, we'll just clear the chat box
    chatBox.empty();
  }

  function saveMessage(forumId, message) {
    // Replace with server-side call
    // For now, we'll just log the message
    console.log("Saving message:", { forumId, message });
  }
  
});
