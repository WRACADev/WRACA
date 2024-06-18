<?php
$servername = "localhost";
$username = "root";
$password = "password";
$dbname = "website_accounts";

// AES key (store this securely and do not hardcode in production)
define('AES_KEY', 'your_aes_key_here');
define('AES_METHOD', 'aes-256-cbc');

// Generate RSA keys (store these securely)
$private_key = openssl_pkey_new(array(
    "private_key_bits" => 2048,
    "private_key_type" => OPENSSL_KEYTYPE_RSA,
));
openssl_pkey_export($private_key, $private_key_pem);
$public_key = openssl_pkey_get_details($private_key)['key'];

file_put_contents('private_key.pem', $private_key_pem);
file_put_contents('public_key.pem', $public_key);

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

function encryptPassword($password) {
    return openssl_encrypt($password, AES_METHOD, AES_KEY, 0, substr(AES_KEY, 0, 16));
}

function decryptPassword($encrypted_password) {
    return openssl_decrypt($encrypted_password, AES_METHOD, AES_KEY, 0, substr(AES_KEY, 0, 16));
}

if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $username = $_POST['username'];
    $encrypted_password = $_POST['password'];
    $personal_info = $_POST['personal_info'] ?? '';
    $favorite_genres = $_POST['favorite_genres'] ?? '';
    $recent_listening_history = $_POST['recent_listening_history'] ?? '';
    $user_type = $_POST['user_type'] ?? 'standard';
    $chat_names = $_POST['chat_names'] ?? '';

    openssl_private_decrypt(base64_decode($encrypted_password), $password, file_get_contents('private_key.pem'));

    if (isset($_POST['register'])) {
        $password_hash = encryptPassword($password);
        $sql = "INSERT INTO users (username, password, personal_info, favorite_genres, recent_listening_history, user_type, chat_names) 
                VALUES ('$username', '$password_hash', '$personal_info', '$favorite_genres', '$recent_listening_history', '$user_type', '$chat_names')";

        if ($conn->query($sql) === TRUE) {
            echo "User registered successfully";
        } else {
            echo "Error: " . $sql . "<br>" . $conn->error;
        }
    }

    if (isset($_POST['login'])) {
        $sql = "SELECT * FROM users WHERE username='$username'";
        $result = $conn->query($sql);

        if ($result->num_rows > 0) {
            $row = $result->fetch_assoc();
            $stored_password = decryptPassword($row['password']);

            if ($stored_password === $password) {
                echo "Login successful";
            } else {
                echo "Invalid username or password";
            }
        } else {
            echo "Invalid username or password";
        }
    }
}

$conn->close();
?>
