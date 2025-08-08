import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Menu, X } from 'lucide-react';

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <nav className="sticky top-0 z-50 shadow-md py-4 px-4 sm:px-6 bg-gradient-to-r from-[#1e1b4b] to-[#312e81] text-white">
      <div className="max-w-7xl mx-auto flex justify-between items-center">
        {/* Logo */}
        <Link to="/" className="text-2xl sm:text-3xl font-extrabold font-mono italic">
          LinkIt
        </Link>

        {/* Hamburger Icon */}
        <div className="sm:hidden">
          <button onClick={() => setMenuOpen(!menuOpen)} className="text-white">
            {menuOpen ? <X size={28} /> : <Menu size={28} />}
          </button>
        </div>

        {/* Navigation Links */}
        <ul
          className={`${
            menuOpen ? 'flex' : 'hidden'
          } flex-col sm:flex sm:flex-row gap-4 sm:gap-8 text-base sm:text-lg font-semibold items-center absolute sm:static top-16 left-0 w-full sm:w-auto 
          py-4 sm:py-0 px-6 sm:px-0 sm:rounded-none shadow-md sm:shadow-none transition-all duration-300
          bg-gradient-to-br from-[#1e1b4b] to-[#312e81] sm:bg-transparent`}
        >
          
          <li>
            <Link to="/" onClick={() => setMenuOpen(false)} className="hover:text-violet-300">
              Home
            </Link>
          </li>
          <li>
            <Link to="/about" onClick={() => setMenuOpen(false)} className="hover:text-violet-300">
              About
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;
