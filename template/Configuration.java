package com.example.springboot;

public class Configuration {
    
    // write get setters

    public List<String> servers;

    public String basePath;

    public String bearerToken;

    public int selectedServerIndex;

    public virtual String getBaseAddress() {    
        StringBuilder sb = new StringBuilder(basePath);
        sb.Append(Servers[selectedServerIndex]);
        return sb.ToString();
    }
}