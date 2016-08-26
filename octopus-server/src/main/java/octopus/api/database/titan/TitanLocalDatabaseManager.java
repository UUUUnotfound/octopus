package octopus.api.database.titan;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

import octopus.api.database.Database;
import octopus.api.database.DatabaseManager;
import octopus.api.projects.OctopusProject;

public class TitanLocalDatabaseManager implements DatabaseManager {

	@Override
	public void initializeDatabaseForProject(OctopusProject project) throws IOException
	{
		String pathToProject = project.getPathToProjectDir();
		String configFile = createConfigurationFile(pathToProject);
		// TitanGraph graph = TitanFactory.open(configFile);
		// graph.close();
	}

	private String createConfigurationFile(String pathToProject) throws IOException
	{
		String dbPath = Paths.get(pathToProject, "database").toAbsolutePath().toString();
		String indexPath = Paths.get(pathToProject, "index").toAbsolutePath().toString();
		String dbConfigFile = Paths.get(pathToProject, "db").toAbsolutePath().toString();

		PrintWriter writer;
		writer = new PrintWriter(dbConfigFile, "UTF-8");
		writer.println("storage.backend=berkeleyje");
		writer.println("index.search.backend=lucene");
		writer.println(String.format("storage.directory=%s", dbPath));
		writer.println(String.format("index.search.directory=%s", indexPath));
		writer.close();
		return dbConfigFile;
	}

	@Override
	public Database getDatabaseInstanceForProject(OctopusProject project)
	{
		TitanLocalDatabase database = new TitanLocalDatabase();
		String dbConfigFile = project.getDBConfigFile();
		TitanGraph graph = TitanFactory.open(dbConfigFile);
		database.setGraph(graph);
		return database;
	}

	@Override
	public void deleteDatabaseForProject(OctopusProject project)
	{
		Database database = getDatabaseInstanceForProject(project);
		TitanLocalDatabase titanDatabase = (TitanLocalDatabase) database;
		String dbPathName = titanDatabase.getPathToDatabase();
		String indexPathName = titanDatabase.getPathToIndex();

		try {
			FileUtils.deleteDirectory(new File(dbPathName));
			FileUtils.deleteDirectory(new File(indexPathName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
