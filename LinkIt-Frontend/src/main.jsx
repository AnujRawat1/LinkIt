import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import HomePage from './components/HomePage.jsx'
import { RoomProvider } from './context/RoomContext.jsx'

createRoot(document.getElementById('root')).render(
    <RoomProvider>
      <App />
    </RoomProvider>
)
