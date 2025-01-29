
import React, { useState, useRef } from "react";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Pie } from "react-chartjs-2";
import "./Analyzer.css";
import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";

ChartJS.register(ArcElement, Tooltip, Legend);

const App = () => {
    const [repoName, setRepoName] = useState("");
    const [inputValue, setInputValue] = useState("");
    const [languagesData, setLanguagesData] = useState({
        labels: [],
        datasets: [
            {
                data: [],
                backgroundColor: [],
                hoverBackgroundColor: [],
            },
        ],
    });
    const [contributorData, setContributorData] = useState({
        labels: [],
        datasets: [
            {
                data: [],
                backgroundColor: [],
                hoverBackgroundColor: [],
            },
        ],
    });
    const [commitsInfo, setCommitsInfo] = useState([]);
    const [duration, setDuration] = useState("");
    const [selectedLanguage, setSelectedLanguage] = useState(null);
    const [languageContributors, setLanguageContributors] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [loading, setLoading] = useState(false); // State for loading main data
    const [showHelperText, setShowHelperText] = useState(true);
    const [isSearched, setIsSearched] = useState(false); // State for handling search state
    const [isLanguageLoading, setIsLanguageLoading] = useState(false); // State for language chart loading
    const chartRef = useRef(null);
    const modalRef = useRef(null);

    const colors = ["#FF6384", "#36A2EB", "#FFCE56", "#4BC0C0", "#9966FF", "#FF9F40"];

    const handleSearch = async () => {

        setLoading(true); // Show loading when search starts
        setShowHelperText(false); // Hide helper text
        console.log("Search started for repository:", inputValue);

        try {
            const match = inputValue.match(
                /https?:\/\/github\.com\/([^/]+)\/([^/]+)/
            );
            if (!match) {
                alert("Please enter a valid GitHub repository URL.");
                setLoading(false); // Hide loading if invalid URL
                return;
            }

            const [_, username, repository] = match;
            console.log("Extracted username and repository:", username, repository);
            setRepoName(repository);

            // API URLs
            const effortApiUrl = `http://localhost:8087/api/effort/${username}/${repository}/analyze`;
            const languagesApiUrl = `http://localhost:8087/api/languages/${username}/${repository}`;
            const durationApiUrl = `http://localhost:8087/api/duration/${username}/${repository}`;

            console.log("Fetching data from APIs...");
            const [effortResponse, languagesResponse, durationResponse] = await Promise.all([
                fetch(effortApiUrl),
                fetch(languagesApiUrl),
                fetch(durationApiUrl),
            ]);

            console.log("Effort API response status:", effortResponse.status);
            console.log("Languages API response status:", languagesResponse.status);
            console.log("Duration API response status:", durationResponse.status);

            const effortData = await effortResponse.json();
            const languages = await languagesResponse.json();
            const durationData = await durationResponse.json();

            console.log("Effort data received:", effortData);
            console.log("Languages data received:", languages);
            console.log("Duration data received:", durationData);

            const contributorEfforts = effortData.contributorEfforts;
            const contributorLabels = Object.keys(contributorEfforts);
            const efforts = contributorLabels.map(
                (label) => contributorEfforts[label].effortPercentage
            );

            console.log("Contributor efforts processed:", efforts);

            const contributorChartColors = contributorLabels.map(
                (_, index) => colors[index % colors.length]
            );

            setContributorData({
                labels: contributorLabels,
                datasets: [
                    {
                        data: efforts,
                        backgroundColor: contributorChartColors,
                        hoverBackgroundColor: contributorChartColors,
                    },
                ],
            });

            const totalContributions = languages.totalContributions;
            const languageLabels = Object.keys(totalContributions);
            const languageValues = Object.values(totalContributions);

            console.log("Language contributions processed:", totalContributions);

            const languageChartColors = languageLabels.map(
                (_, index) => colors[index % colors.length]
            );

            setLanguagesData({
                labels: languageLabels,
                datasets: [
                    {
                        data: languageValues,
                        backgroundColor: languageChartColors,
                        hoverBackgroundColor: languageChartColors,
                    },
                ],
            });

            const commits = contributorLabels.map((contributor) => ({
                name: contributor,
                count: contributorEfforts[contributor]?.commits || 0,
            }));

            console.log("Commits info processed:", commits);
            setCommitsInfo(commits);

            const durationInDays = durationData.durationInDays;
            const months = Math.floor(durationInDays / 30);
            setDuration(`DURATION: ${durationInDays} days (${months} month${months !== 1 ? "s" : ""})`);

            console.log("Duration processed:", duration);
            setLoading(false); // Hide loading once data is fetched
            setIsSearched(true); // Set the search state to true, allowing components to show
        } catch (error) {
            console.error("Error fetching or processing data:", error);
            alert("Failed to fetch data. Please try again.");
            setLoading(false); // Hide loading on error
        }
    };

    const handleLanguageClick = async (event) => {
        const chart = chartRef.current;
        if (!chart) return;

        const elements = chart.getElementsAtEventForMode(
            event.nativeEvent,
            "nearest",
            { intersect: true },
            true
        );

        if (elements.length > 0) {
            const clickedIndex = elements[0].index;
            const language = languagesData.labels[clickedIndex];

            console.log("Clicked on language:", language);

            // Set the language loading state to true before fetching contributors
            setIsLanguageLoading(true);

            try {
                const match = inputValue.match(
                    /https?:\/\/github\.com\/([^/]+)\/([^/]+)/
                );
                if (!match) {
                    alert("Invalid repository URL. Please re-enter.");
                    setIsLanguageLoading(false); // Hide loading if invalid URL
                    return;
                }

                const [_, username, repository] = match;
                const languagesApiUrl = `http://localhost:8087/api/languages/${username}/${repository}`;

                console.log("Fetching language contributors for language...", language);
                const languagesResponse = await fetch(languagesApiUrl);
                const languages = await languagesResponse.json();

                console.log("Language contributors data received:", languages);

                const languageData = languages.percentages;
                const contributors = Object.entries(languageData).map(
                    ([contributor, langData]) => ({
                        name: contributor,
                        percentage: langData[language] || 0,
                    })
                );

                console.log("Processed language contributors:", contributors);

                setSelectedLanguage({ language });
                setLanguageContributors(contributors);
                setIsModalOpen(true);
                setIsLanguageLoading(false); // Hide loading once data is loaded
            } catch (error) {
                console.error("Error fetching language contributors:", error);
                alert("Failed to fetch contributors for this language.");
                setIsLanguageLoading(false); // Hide loading on error
            }
        }
    };

    const closeModal = (event) => {
        if (modalRef.current && !modalRef.current.contains(event.target)) {
            setIsModalOpen(false);
        }
    };

    React.useEffect(() => {
        if (isModalOpen) {
            document.addEventListener("click", closeModal);
        } else {
            document.removeEventListener("click", closeModal);
        }
        return () => document.removeEventListener("click", closeModal);
    }, [isModalOpen]);

    const handleKeyDown = (e) => {
        if (e.key === 'Enter') {
            handleSearch();
        }
    };

    return (
        <div className="app-container">
            <h1 className="header-title">GitHub Repository Analyzer</h1>
            <section className="search-section">
                <input
                    type="text"
                    className="repo-input"
                    placeholder="Enter your GitHub repository link here..."
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    onKeyDown={handleKeyDown}
                    autoFocus
                />
                <button className="search-btn" onClick={handleSearch}>
                    Start
                </button>
                {showHelperText && (
                    <p className="search-helper-text" id="helper-text">
                        Unlock insights about <strong>commits</strong>, <strong>language usage</strong>,
                        <strong>contributors efforts</strong>, and much more by analyzing your repository.
                    </p>
                )}
            </section>

            {loading ? (
                    <div className="skeleton-card">

                        <Skeleton height={30} width={200} className="mb-4" />

                        <div className="skeleton-grid">
                            <div className="skeleton-chart">
                                <Skeleton height={25} width={150} className="mt-3" />
                                <Skeleton circle height={250} width={250} />
                            </div>

                            <div className="skeleton-text">
                                <Skeleton height={25} width={150} className="mb-2" />
                                <Skeleton height={15} width={180} className="mb-1" />
                                <Skeleton height={15} width={140} className="mb-1" />
                                <Skeleton height={15} width={160} className="mb-1" />
                            </div>

                            <div className="skeleton-chart">
                                <Skeleton height={25} width={150} className="mt-3" />
                                <Skeleton circle height={250} width={250} />
                            </div>
                        </div>
                    </div>
            ) : (
                isSearched && inputValue && (
                    <section className="dashboard">
                        <section className="repo-display">
                            <h2>{repoName}</h2>
                        </section>
                        <div className="stats-container">
                            <div className="chart">
                                <h3>-:LANGUAGES:-</h3>
                                <Pie
                                    ref={chartRef}
                                    data={languagesData}
                                    onClick={handleLanguageClick}
                                />
                            </div>

                            <div className="commits-and-duration">
                                <div id="commits-info">
                                    <h3>-:COMMITS:-</h3>
                                    {commitsInfo.map((commit, index) => (
                                        <p key={index}>
                                            {commit.name} - {commit.count} COMMITS
                                        </p>
                                    ))}
                                </div>
                                <div className="duration-info">
                                    <p>{duration}</p>
                                </div>
                            </div>

                            <div className="chart">
                                <h3>-:CONTRIBUTERS:-</h3>
                                <Pie data={contributorData}/>
                            </div>
                        </div>
                    </section>
                )
            )}

            {isLanguageLoading && (
                <section className="loading-section">
                    <p>Loading language contributors, please wait...</p>
                </section>
            )}

            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content" ref={modalRef}>
                        <h2>{selectedLanguage.language} Contributors</h2>
                        <Pie
                            data={{
                                labels: languageContributors.map((contributor) => contributor.name),
                                datasets: [
                                    {
                                        data: languageContributors.map((contributor) => contributor.percentage),
                                        backgroundColor: languageContributors.map(
                                            (_, index) => colors[index % colors.length]
                                        ),
                                        hoverBackgroundColor: languageContributors.map(
                                            (_, index) => colors[index % colors.length]
                                        ),
                                    },
                                ],
                            }}
                        />
                    </div>
                </div>
            )}
        </div>
    );
};

export default App;
