import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './components/Home';
import Comparision from './components/Comparision';
import Find from './components/Find';
import History from './components/History';

const App = () => {
    return (
        <Router>
            <Navbar />
            <div className="container">
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/Comparision" element={<Comparision />} />
                    <Route path="/Find" element={<Find />} />
                    <Route path="/History" element={<History />} />
                </Routes>
            </div>
        </Router>
    );
};

export default App;

//npm run dev