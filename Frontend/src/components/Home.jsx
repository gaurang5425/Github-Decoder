import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Home.css";

const Home = () => {
    const [username, setUsername] = useState("");
    const [repos, setRepos] = useState([]);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const fetchRepos = async () => {
        if (!username.trim()) return;
        setLoading(true);
        try {
            const response = await fetch(
                `http://localhost:8087/api/github/repositories/${username}`
            );

            console.log("Repository API response status:", response.status);

            if (!response.ok) throw new Error("Failed to fetch");
            const repoData = await response.json();
            setRepos(repoData);

            console.log("Repository data received:", repoData);
        } catch (error) {
            console.error("Error fetching repositories:", error);
            alert('User not found or an error occurred: ' + error.message);
            setRepos([]);
        } finally {
            setLoading(false);
        }
    };

    const truncateText = (text, maxLength = 20) =>
        text.length > maxLength ? text.substring(0, maxLength) + "..." : text;

    const handleKeyDown = (e) => {
        if (e.key === "Enter") {
            fetchRepos();
        }
    };

    const handleCardClick = (repoUrl) => {
        navigate(`/analyzer?repo=${encodeURIComponent(repoUrl)}`);
    };

    return (
        <div className="app-container">
            <section className="search-section">
                <input
                    type="text"
                    className="repo-input"
                    placeholder="Enter GitHub username..."
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    onKeyDown={handleKeyDown}
                    autoFocus
                />
                <button className="search-btn" onClick={fetchRepos}>Start</button>
            </section>
            <div className="repo-grid">
                {loading ? (
                    [...Array(12)].map((_, index) => (
                        <div key={index} className="repo-card skeleton">
                            <div className="Home-skeleton-text skeleton-name"></div>
                            <div className="Home-skeleton-text skeleton-url"></div>
                            <div className="Home-skeleton-text skeleton-lang"></div>
                        </div>
                    ))
                ) : (
                    repos.map((repo, index) => (
                        <div
                            key={index}
                            className="repo-card"
                            onClick={() => handleCardClick(repo.html_url)}
                            style={{ cursor: "pointer" }}
                        >
                            <p className="repo-name">{truncateText(repo.name)}</p>
                            <a
                                href={repo.html_url}
                                className="repo-url"
                                target="_blank"
                                rel="noopener noreferrer"
                                onClick={(e) => e.stopPropagation()}
                            >
                                {truncateText(repo.html_url, 25)}
                            </a>
                            <p className="repo-language">{repo.language || "Unknown"}</p>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default Home;
