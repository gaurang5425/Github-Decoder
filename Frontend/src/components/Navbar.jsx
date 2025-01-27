import React from 'react';
import {Link} from 'react-router-dom';
import './Navbar.css';

const Navbar = () => {
    return (
        <header className="header">
            <h1 className="title">GIT - REPOSITORY - DECODER</h1>
            <nav className="navbar">
                <ul className="nav-list">
                    <li>
                        <Link to="/Home">Home</Link>
                    </li>
                    <li>
                        <Link to="/">Analyzer</Link>
                    </li>
                    <li>
                        <Link to="/Find">Find</Link>
                    </li>
                    <li>
                        <Link to="/Comparision">Comparision</Link>
                    </li>
                </ul>
            </nav>
        </header>
    );
};

export default Navbar;
