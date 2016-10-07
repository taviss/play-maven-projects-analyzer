package controllers;

import models.Project;
import models.Sonar;
import models.dao.ProjectDAO;
import models.dao.SonarDAO;
import play.Configuration;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import utils.OSUtils;
import utils.ZipUtils;
import views.html.project;
import views.html.report;
import views.html.upload;
import javax.inject.Inject;
import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.zip.ZipException;

/**
 * Created by octavian.salcianu on 8/17/2016.
 */
public class ProjectController extends Controller {
    @Inject
    private FormFactory formFactory;

    @Inject
    private ProjectDAO projectDAO;

    @Transactional
    public Result uploadProject() {
        Form<Project> projectForm = formFactory.form(Project.class).bindFromRequest();
        if (!projectForm.hasErrors()) {
            Project project = projectForm.get();
            Http.MultipartFormData<File> body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart<File> projectFile = body.getFile("project");
            //List<ValidationError> errors = new ArrayList<ValidationError>();
            //Set private fields access for returning errors array as json
            
            if (projectFile != null) {
                String fileName = projectFile.getFilename();
                String contentType = projectFile.getContentType();
                if (!contentType.equals("application/x-zip-compressed")) {
                    projectForm.reject("file", "File is not .zip");
                    return badRequest(projectForm.errorsAsJson());
                }
                try {
                    File file = projectFile.getFile();

                    if (!ZipUtils.existsInZip(file, "pom.xml")) {
                        projectForm.reject("file", "No pom.xml found");
                        return badRequest(projectForm.errorsAsJson());
                    }


                    //Get the project name
                    String projectKey = null, projectName = null;
                    try {
                        projectKey = ZipUtils.getProjectDetailsFromZip(file)[0];
                        projectName = ZipUtils.getProjectDetailsFromZip(file)[1];
                    } catch (NullPointerException e) {
                        projectForm.reject("file", "Missing groupId, artifactId or name from pom.xml");
                        return badRequest(projectForm.errorsAsJson());
                    }

                    //Set up the file and get the upload path
                    String uploadPath = Configuration.root().getString("uploadPath").replaceAll("/", Matcher.quoteReplacement(File.separator));

                    //Construct the date
                    Date now = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("(dd-MM-yyyy)(HH-mm-ss)");

                    //Set up the file name and move to uploads directory
                    String filePath = dateFormat.format(now) + project.getName().replaceAll("\\s+", "_") + "-" + fileName;
                    file.renameTo(new File(uploadPath, filePath));
                    project.setInputDate(now);
                    project.setFileName(filePath);
                    project.setProjectName(projectName);
                    project.setProjectKey(projectKey);
                    
                    projectDAO.create(project);

                    return ok("File uploaded");
                } catch(XMLStreamException e) {
                    Logger.error("Error while reading from the XML file");
                    projectForm.reject("file", "Error while reading XML file, please check the format");
                    return badRequest(projectForm.errorsAsJson());
                } catch (ZipException e) {
                    Logger.error("Error opening zip file");
                    projectForm.reject("file", "Error opening zip file, please try again!");
                    return badRequest(projectForm.errorsAsJson());
                } catch(IOException e) {
                    Logger.error("Error while uploading project, check if the upload path is correct and the upload folder exists");
                    projectForm.reject("file", "Error while uploading file, please try again!");
                    return badRequest(projectForm.errorsAsJson());
                }
            } else {
                projectForm.reject("file", "No file attached");
                return badRequest(projectForm.errorsAsJson());
            }
        } else {
            return badRequest(projectForm.errorsAsJson());
        }
    }

    @Transactional
    public Result upload() {
        return ok(upload.render());
    }

    @Transactional
    public Result uploadReport() { return ok(report.render()); }

    @Transactional
    public Result getAll(int page) {
        
        return ok(Json.toJson(projectDAO.getPage(page)));
    }

    @Transactional
    public Result getProject(Long id) {
        
        Project proj = projectDAO.get(id);
        if(proj != null) {
            return ok(Json.toJson(proj));
        } else {
            return badRequest("No such project");
        }
    }

    @Security.Authenticated(Secured.class)
    @Transactional
    public Result viewProject(Long id) {
        return ok(project.render());
    }

    @Transactional
    public Result getProjects(int page) {
        
        Set<Map.Entry<String,String[]>> queryString = request().queryString().entrySet();
        return(ok(Json.toJson(projectDAO.findByName(page, queryString))));
    }

    @Transactional
    @Security.Authenticated(Secured.class)
    public Result deleteProject(Long id) {
        
        Project project = projectDAO.get(id);
        if(project != null) {
            String unzipPath = Configuration.root().getString("unzipPath").replaceAll("/", Matcher.quoteReplacement(File.separator)) + project.getFileName().split("\\.")[0];
            String archivePath = Configuration.root().getString("uploadPath").replaceAll("/", Matcher.quoteReplacement(File.separator)) + project.getFileName();
            File archive = new File(archivePath);
            archive.delete();
            Path unzipFolder = Paths.get(unzipPath);
            if(Files.exists(unzipFolder)) {
                try {
                    Files.walkFileTree(unzipFolder, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path directory, IOException exc) throws IOException {
                            Files.delete(directory);
                            return FileVisitResult.CONTINUE;
                        }

                    });
                } catch (IOException e) {
                    Logger.info(e.getMessage());
                    return badRequest("Error while deleting files");
                }
            }
            projectDAO.delete(project);
            Logger.info("User " + session().get("user") + "(" + request().remoteAddress() + ") has deleted project " + project.getProjectName());
            return ok("Project deleted");
        } else {
            return notFound("Project not found");
        }

    }

    @Transactional
    @Security.Authenticated(Secured.class)
    public Result analyzeProject(Long id) {
        //Find the project
        
        Project project = projectDAO.get(id);
        if(project != null) {
            ///Unzip the archive
            String unzipPath = Configuration.root().getString("unzipPath").replaceAll("/", Matcher.quoteReplacement(File.separator)) + project.getFileName().split("\\.")[0];
            String archivePath = Configuration.root().getString("uploadPath").replaceAll("/", Matcher.quoteReplacement(File.separator)) + project.getFileName();
            try {
                ZipUtils.unzip(archivePath, unzipPath);
            } catch (IOException e) {
                Logger.error("Unzip failed: " + e.getMessage());
                return badRequest("Unzip failed");
            }

            //Sonar analysis
            try {
                DynamicForm form = formFactory.form().bindFromRequest();
                String sonarAddress = null, sonarLogin = null, sonarPassword = null;
                SonarDAO sonarDAO = new SonarDAO();
                Long sonarId = Long.valueOf(form.get("sonar"));
                Logger.info(sonarId.toString());
                Sonar sonar = sonarDAO.get(sonarId);
                if (sonar == null) {
                    sonarAddress = Configuration.root().getString("sonar.host");
                    sonarLogin = Configuration.root().getString("sonar.user");
                    sonarPassword = Configuration.root().getString("sonar.password");
                } else {
                    sonarAddress = sonar.getAddress();
                    sonarLogin = sonar.getUser();
                    sonarPassword = sonar.getPassword();
                }

                if (OSUtils.isUnix()) {
                    ProcessBuilder builder = new ProcessBuilder(
                            "/bin/bash", "-c", Configuration.root().getString("filesDirectory") + " && cd \"" + unzipPath + "\" && mvn sonar:sonar -Dsonar.host.url=" + sonarAddress + " -Dsonar.login=" + sonarLogin + " -Dsonar.password=" + sonarPassword);

                    builder.redirectErrorStream(true);
                    Process p = builder.start();
                    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while (true) {
                        line = r.readLine();
                        if (line == null) {
                            break;
                        }
                        Logger.info(line);
                    }
                } else if (OSUtils.isWindows()) {

                    ProcessBuilder builder = new ProcessBuilder(
                            "cmd.exe", "/c", Configuration.root().getString("filesDirectory") + " && cd \"" + unzipPath + "\" && mvn sonar:sonar -Dsonar.host.url=" + sonarAddress + " -Dsonar.login=" + sonarLogin + " -Dsonar.password=" + sonarPassword);
                    builder.redirectErrorStream(true);
                    Process p = builder.start();
                    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while (true) {
                        line = r.readLine();
                        if (line == null) {
                            break;
                        }
                        Logger.info(line);
                    }
                }
            } catch (Exception e) {
                Logger.error("Error while analyzing project:" + e.getMessage());
            }

            return ok();
        } else {
            return notFound();
        }
    }

    /* sonar-scanner
    //Create the properties file
    Properties prop = new Properties();
    FileInputStream in = null;
    try {
        //Get the pom.xml file and read the source directory
        String sourceDirectory = null;
        File root = new File(unzipPath);
        Collection files = FileUtils.listFiles(root, null, true);
        for (Iterator iterator = files.iterator(); iterator.hasNext();) {
            File file = (File) iterator.next();
            if (file.getName().equals("pom.xml")) {
                File xmlFile = new File(file.getAbsolutePath());
                FileInputStream fileInput = new FileInputStream(file);
                Properties properties = new Properties();
                properties.loadFromXML(fileInput);
                fileInput.close();

                sourceDirectory = properties.getProperty("sourceDirectory");

            }

        }

        //If there isn't a source directory defined, take the whole folder
        if(sourceDirectory == null)
            sourceDirectory = ".";

        String propFilePath = unzipPath + File.separator + "sonar-project.properties";
        File propFile = new File(propFilePath);
        propFile.createNewFile();
        in = new FileInputStream(propFile);
        prop.load(in);
        prop.setProperty("sonar.projectName", project.getName());
        prop.setProperty("sonar.projectKey", project.getName().toLowerCase().replaceAll("\\s", ":"));
        prop.setProperty("sonar.projectVersion", "1.0");
        prop.setProperty("sonar.sources", sourceDirectory);
        prop.store(new FileOutputStream(propFilePath), null);

        //Sonar analysis
        try {
            if (OSUtils.isUnix()) {
                //String statement = new String[] { "/bin/bash", "-c", "mvn clean install" };
                //Process p = Runtime.getRuntime().exec(statement);
            } else if (OSUtils.isWindows()) {
                ProcessBuilder builder = new ProcessBuilder(
                        "cmd.exe", "/c", Configuration.root().getString("filesDirectory") + " && cd \"" + unzipPath + "\" && sonar-scanner");
                builder.redirectErrorStream(true);
                Process p = builder.start();

                    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    while (true) {
                        line = r.readLine();
                        if (line == null) { break; }
                        Logger.info(line);
                    }
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    } catch (IOException e) {
        return badRequest("Sonar properties file missing or failed operation");
    } finally {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                //
            }
        }
    }
    */
}
