package jelixeclipse.wizards;

import org.eclipse.core.resources.*;

public class myProject {

	// Class members
	private IProject currentProject;

	/***************************************************************************
	 * Constructor
	 **************************************************************************/
	public myProject() {
		findCurrentOpenProject();
	}

	/***************************************************************************
	 * Accessor
	 **************************************************************************/
	public IProject getCurrentProject() {
		return currentProject;
	}

	/***************************************************************************
	 * FindCurrentOpenProject
	 * 
	 * Search for a open project. Open project is the current in use.
	 **************************************************************************/
	private void findCurrentOpenProject() {
		/***********************************************************************
		 * Get the list of projects. It's the root Java element that
		 * corresponding to the workspace. Give access to the projects list.
		 */
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		IProject[] allProjects;

		try {
			allProjects = root.getProjects(); // Get all projects
		} catch (Exception e) {
			System.out.println(e.toString());
			return;
		}

		/* Search the current open project and stop when he find */
		for (IProject project : allProjects)
			if (project.isOpen()) {
				// currentProject est déclaré de type IProject
				currentProject = project.getProject();
				return;
			}
	}

}
