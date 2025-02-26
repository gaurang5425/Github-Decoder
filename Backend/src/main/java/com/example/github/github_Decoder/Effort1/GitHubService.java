package com.example.github.github_Decoder.Effort1;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubService {
    private final RestTemplate restTemplate;
    
    @Value("${github.token}")
    private String githubToken;
    
    public GitHubService() {
        this.restTemplate = new RestTemplate();
    }
    
    public List<Map<String, Object>> getContributors(String owner, String repo) {
        String url = String.format("https://api.github.com/repos/%s/%s/contributors", owner, repo);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        return response.getBody();
    }
    
    public Map<String, Long> getLanguages(String owner, String repo) {
        String url = String.format("https://api.github.com/repos/%s/%s/languages", owner, repo);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        
        ResponseEntity<Map<String, Long>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<Map<String, Long>>() {}
        );
        
        return response.getBody();
    }
    
    /**
     * Get all files in a repository with their language and contributors
     * 
     * @param owner Repository owner
     * @param repo Repository name
     * @return List of files with metadata
     */
    public List<Map<String, Object>> getRepositoryFiles(String owner, String repo) {
        // First get the main contents of the repo
        List<Map<String, Object>> allFiles = new ArrayList<>();
        getContentsRecursively(owner, repo, "", allFiles);
        
        // For each file, get the language and contributors
        for (Map<String, Object> file : allFiles) {
            // Only process actual files (not directories)
            if ("file".equals(file.get("type"))) {
                String path = (String) file.get("path");
                String language = detectLanguage(path);
                file.put("language", language);
                
                // Get contributors for this file
                List<String> fileContributors = getFileContributors(owner, repo, path);
                file.put("contributors", fileContributors);
            }
        }
        
        // Filter to include only files, not directories
        List<Map<String, Object>> onlyFiles = new ArrayList<>();
        for (Map<String, Object> item : allFiles) {
            if ("file".equals(item.get("type"))) {
                onlyFiles.add(item);
            }
        }
        
        return onlyFiles;
    }
    
    /**
     * Recursively get all contents of a repository, including in subdirectories
     */
    private void getContentsRecursively(String owner, String repo, String path, List<Map<String, Object>> allFiles) {
        String url;
        if (path.isEmpty()) {
            url = String.format("https://api.github.com/repos/%s/%s/contents", owner, repo);
        } else {
            url = String.format("https://api.github.com/repos/%s/%s/contents/%s", owner, repo, path);
        }
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        List<Map<String, Object>> contents = response.getBody();
        if (contents != null) {
            for (Map<String, Object> content : contents) {
                String type = (String) content.get("type");
                allFiles.add(content);
                
                // If it's a directory, recursively get its contents
                if ("dir".equals(type)) {
                    String dirPath = (String) content.get("path");
                    getContentsRecursively(owner, repo, dirPath, allFiles);
                }
            }
        }
    }
    
    /**
     * Detect the language of a file based on its extension
     */
    private String detectLanguage(String filePath) {
        Map<String, String> extensionToLanguage = new HashMap<>();
        extensionToLanguage.put("java", "Java");
        extensionToLanguage.put("py", "Python");
        extensionToLanguage.put("js", "JavaScript");
        extensionToLanguage.put("ts", "TypeScript");
        extensionToLanguage.put("html", "HTML");
        extensionToLanguage.put("css", "CSS");
        extensionToLanguage.put("cpp", "C++");
        extensionToLanguage.put("c", "C");
        extensionToLanguage.put("h", "C");
        extensionToLanguage.put("rb", "Ruby");
        extensionToLanguage.put("php", "PHP");
        extensionToLanguage.put("go", "Go");
        extensionToLanguage.put("rs", "Rust");
        extensionToLanguage.put("swift", "Swift");
        extensionToLanguage.put("kt", "Kotlin");
        extensionToLanguage.put("sql", "SQL");
        
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            String extension = filePath.substring(lastDotIndex + 1).toLowerCase();
            return extensionToLanguage.getOrDefault(extension, "Other");
        }
        
        return "Other";
    }
    
    /**
     * Get contributors for a specific file
     */
    public List<String> getFileContributors(String owner, String repo, String path) {
        String url = String.format("https://api.github.com/repos/%s/%s/commits", owner, repo);
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        
        // Add path parameter to get commits for a specific file
        url += "?path=" + path;
        
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(headers),
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        List<Map<String, Object>> commits = response.getBody();
        List<String> contributors = new ArrayList<>();
        
        if (commits != null) {
            for (Map<String, Object> commit : commits) {
                Map<String, Object> author = (Map<String, Object>) commit.get("author");
                if (author != null) {
                    String login = (String) author.get("login");
                    if (login != null && !contributors.contains(login)) {
                        contributors.add(login);
                    }
                }
            }
        }
        
        return contributors;
    }
}