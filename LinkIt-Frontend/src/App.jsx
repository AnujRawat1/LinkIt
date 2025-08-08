import React from 'react';
import './App.css';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import HomePage from './components/HomePage'; // ✅ used in route
import Navbar from './components/Navbar';
import About from './components/About';
import Room from './components/Room';

import { ThemeProvider } from './ThemeContext';

const router = createBrowserRouter([
  {
    path: '/',
    element: (
      <div>
        <Navbar />
        <HomePage />
      </div>
    ),
  },
  {
    path: '/about',
    element: (
      <div>
        <Navbar />
        <About />
      </div>
    ),
  },
  {
    path: "/room/:roomId",
    element: (
      <div>
        <Navbar />
        <Room/>
      </div>
    ),
  },
]);

function App() {
  return (
    <>
        <RouterProvider router={router} />
        <ToastContainer position="top-right" autoClose={1200} />
    </>
  );
}

export default App;
