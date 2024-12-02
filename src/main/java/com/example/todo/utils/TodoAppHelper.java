package com.example.todo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import com.example.todo.config.todoapp.TodoAppCofiguration;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoAppHelper {

    private final TodoAppCofiguration todoAppCofiguration;

    /**
     * Restarts the Todo App container by stopping and then starting it.
     */
    public void restartTodoApp() {
        var name = getRunningContainerNameByImage(todoAppCofiguration.getImageName());
        restartPodmanContainer(name);
    }

    /**
     * Restarts the specified Podman container by stopping and then starting it.
     *
     * @param containerName the name of the Podman container to restart
     * @throws RuntimeException if an IOException or InterruptedException occurs
     */
    public void restartPodmanContainer(String containerName) {
        try {
            ProcessBuilder stopBuilder = new ProcessBuilder("podman", "stop", containerName);
            stopBuilder.inheritIO();
            Process stopProcess = stopBuilder.start();
            stopProcess.waitFor();

            ProcessBuilder startBuilder = new ProcessBuilder("podman", "start", containerName);
            startBuilder.inheritIO();
            Process startProcess = startBuilder.start();
            startProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to restart podman container: " + containerName, e);
        }
    }
    
    /**
     * Gets the name of the running Podman container by its image name.
     *
     * @param imageName the image name of the Podman container
     * @return the name of the running container, or null if not found
     * @throws RuntimeException if an IOException or InterruptedException occurs
     */
    public String getRunningContainerNameByImage(String imageName) {
        try {
            ProcessBuilder psBuilder = new ProcessBuilder("podman", "ps", "--format", "{{.Names}} {{.Image}}");
            psBuilder.redirectErrorStream(true);
            Process psProcess = psBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(psProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(imageName)) {
                    return line.split(" ")[0]; // Return the first part, which is the container name
                }
            }
            psProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to get running container name by image: " + imageName, e);
        }
        return null; // Return null if no container with the provided image is found
    }
}
