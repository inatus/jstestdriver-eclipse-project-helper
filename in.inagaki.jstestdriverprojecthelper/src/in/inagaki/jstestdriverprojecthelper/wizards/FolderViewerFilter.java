package in.inagaki.jstestdriverprojecthelper.wizards;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class FolderViewerFilter extends ViewerFilter {
	private static final Class<?>[] acceptedClasses = {IProject.class, IFolder.class};
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		for (Class<?> clazz : acceptedClasses) {
			if (clazz.isInstance(element)) {
				return true;
			}
		}
		return false;
	}

}
