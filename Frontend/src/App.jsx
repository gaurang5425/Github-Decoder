import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Analyzer from './components/Analyzer.jsx';
import Comparision from './components/Comparision';
import Find from './components/Find';
import Home from './components/Home.jsx';

const App = () => {
    return (
        <Router>
            <Navbar />
            <div className="container">
                <Routes>
                    <Route path="/" element={<Analyzer />} />
                    <Route path="/Home" element={<Home />} />
                    <Route path="/Analyzer" element={<Analyzer />} />
                    <Route path="/Find" element={<Find />} />
                    <Route path="/Comparision" element={<Comparision />} />
                </Routes>
            </div>
        </Router>
    );
};

export default App;

//npm run dev