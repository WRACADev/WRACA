
const express = require('express');
const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const bodyParser = require('body-parser');
const http = require('http');
const socketIo = require('socket.io');

const app = express();
const server = http.createServer(app);
const io = socketIo(server);

app.use(bodyParser.json());
app.use(express.static('public'));

mongoose.connect('mongodb://localhost:27017/myDatabase', {
    useNewUrlParser: true,
    useUnifiedTopology: true,
});

const userSchema = new mongoose.Schema({
    username: { type: String, unique: true },
    password: String,
});

const User = mongoose.model('User', userSchema);

app.post('/register', async (req, res) => {
    const { username, password } = req.body;
    const hashedPassword = await bcrypt.hash(password, 10);
    const user = new User({ username, password: hashedPassword });
    await user.save();
    res.send('User registered');
});

app.post('/login', async (req, res) => {
    const { username, password } = req.body;
    const user = await User.findOne({ username });
    if (user && await bcrypt.compare(password, user.password)) {
        res.send('Login successful');
    } else {
        res.send('Invalid username or password');
    }
});

io.on('connection', (socket) => {
    console.log('New client connected');
    socket.on('message', (message) => {
        io.emit('message', message);
    });
    socket.on('disconnect', () => {
        console.log('Client disconnected');
    });
});

server.listen(3000, () => {
    console.log('Server running on port 3000');
});
