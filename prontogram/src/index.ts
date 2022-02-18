import express from 'express'
import initializeDB from './database'
import { isEmpty } from './maybe'

const app = express()
const PORT = process.env['PORT'] || 4000
var cors = require('cors')


app.use(cors());

// parse application/x-www-form-urlencoded
app.use(express.urlencoded({ extended: false }))

// parse application/json
app.use(express.json())

const database = initializeDB()

// app.get('/', (_req, res) => res.send('Server is running...'))

// Richiesta del i-esimo messaggio da parte del numero "receiver"
app.get('/api/messages/:receiver/:messageId', (req, res) =>
{

  // Website you wish to allow to connect
  res.setHeader('Access-Control-Allow-Origin', '*');

  // Request methods you wish to allow
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');

  // Request headers you wish to allow
  res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');

  const receiver = req.params.receiver
  const messageID = Number.parseInt(req.params.messageId)

  const message = database.read(receiver, messageID)

  /* The message doesn't exist */
  if (isEmpty(message)) {
    res.status(404).json({
      statusCode: 404,
      exists: false
    })
    return
  }

  /* Message return successfully */
  res.json({
    statusCode: 200,
    exists: true,
    text: message
  })
})

app.get('/api/reference', (_req, res) =>
{
  const reference = database.reference()

  /* Reference created and return successfully */
  res.json({
    statusCode: 200,
    reference: reference
  })
})

// funzione per mandare il messaggio al receiver
app.post('/api/messages/:receiver', (req, res) =>
{
  const receiver = req.params.receiver
  const reference = req.body?.reference
  const sender = req.body?.sender
  const text = req.body?.text

  /* Missing POST parameters */
  if (!reference || !sender || !text) {
    res.status(400).json({
      statusCode: 400,
      error: true
    })
    return
  }

  const created = database.create({ receiver, sender, text }, reference)

  /* Invalid reference number (already used or expired) */
  if (isEmpty(created)) {
    res.status(410).json({
      statusCode: 410,
      error: true
    })
    return
  }

  /* Message successfully created */
  res.json({
    statusCode: 200,
    error: false
  })
})

app.listen(PORT, () => {
  console.log(`⚡️[server]: Server is running at https://localhost:${PORT}`)
})
