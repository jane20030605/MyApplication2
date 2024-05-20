// 引入必要的模組
const express = require('express'); // 引入 Express 框架
const mysql = require('mysql'); // 引入 MySQL 模組
const bodyParser = require('body-parser'); // 引入 body-parser 模組，用於解析 POST 請求的內容

// 創建 Express 應用程式實例
const app = express();

// 使用 body-parser 中間件解析請求主體
app.use(bodyParser.json());

// 建立與 MySQL 資料庫的連接
const connection = mysql.createConnection({
    host: '26.110.164.151', // MySQL 主機名
    user: 'root', // MySQL 使用者名
    password: 'AMY911002', // MySQL 密碼
    database: 'appdata' // 資料庫名稱
});

// 連接至 MySQL 資料庫
connection.connect();

// 處理 GET 請求，獲取資料庫中的資料
app.get('/data', (req, res) => {
    connection.query('SELECT * FROM example_table', (error, results) => {
        if (error) throw error; // 若發生錯誤，拋出例外
        res.json(results); // 將查詢結果以 JSON 格式返回給客戶端
    });
});

// 處理 POST 請求，將資料插入到資料庫中
app.post('/data', (req, res) => {
    const data = req.body; // 從請求主體中獲取資料
    connection.query('INSERT INTO example_table SET ?', data, (error, results) => {
        if (error) throw error; // 若發生錯誤，拋出例外
        res.status(201).json(results); // 返回剛插入的資料
    });
});

// 監聽指定端口，啟動伺服器
app.listen(3000, () => {
    console.log('Server is running on port 3000'); // 在控制台中輸出啟動訊息
});
