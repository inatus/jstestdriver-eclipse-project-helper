package in.inagaki.jstestdriverprojecthelper.wizards;

import in.inagaki.jstestdriverprojecthelper.Activator;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * The "New" wizard page creates libraries and a configuration file for
 * JsTestDriver and stores JsTestDriver settings.
 */
public class JsTestDriverProjectConfigurationPage extends WizardPage {
	private static final int FIRST_SEGMENT = 0;
	private static final String dirSeparator = "/";
	private static final String defaultSourceFolder = "/src";
	private static final String defaultTestSourceFolder = "/test";
	private static final String defaultTestLibraryFolder = "/jstestlib";

	private final IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
			.getRoot();

	private Text sourceFolder;
	private Text testSourceFolder;
	private ISelection selection;
	private Label lblsourceFolder;
	private Text port;
	private Text pathToIE;
	private GridData gd_sourceFolder;
	private GridData gd_testSourceFolder;
	private Text pathToFireFox;
	private Text pathToChrome;
	private Text pathToOpera;
	private Text pathToSafari;
	private Label lblTestSourceFolder;
	private Label lblpathToIE;
	private Label lblpathToFirefox;
	private Label lblpathToChrome;
	private Label lblpathToOpera;
	private Label lblpathToSafari;
	private Label lblTestLibraryFolder;
	private Text testLibraryFolder;
	private GridData gd_testLibrayFolder;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public JsTestDriverProjectConfigurationPage(ISelection selection) {
		super("wizardPage");
		setTitle("JsTestDriver Project Configuration");
		setDescription("This wizard creates libraries and a configuration file for JsTestDriver and stores JsTestDriver settings.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite parentContainer = new Composite(parent, SWT.NULL);
		parentContainer.setLayout(new GridLayout(1, false));

		Composite container = new Composite(parentContainer, SWT.NULL);
		GridData gd_container = new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1);
		gd_container.widthHint = 477;
		container.setLayoutData(gd_container);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		lblsourceFolder = new Label(container, SWT.NULL);
		lblsourceFolder.setText("&Source folder:");

		sourceFolder = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd_sourceFolder = new GridData(GridData.FILL_HORIZONTAL);
		sourceFolder.setLayoutData(gd_sourceFolder);
		sourceFolder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button sourceFolderButton = new Button(container, SWT.PUSH);
		sourceFolderButton.setText("Browse...");
		sourceFolderButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleFolderSelection(sourceFolder);
			}
		});

		lblTestSourceFolder = new Label(container, SWT.NONE);
		lblTestSourceFolder.setText("&Test source folder:");

		testSourceFolder = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd_testSourceFolder = new GridData(GridData.FILL_HORIZONTAL);
		testSourceFolder.setLayoutData(gd_testSourceFolder);
		testSourceFolder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button testSourceFolderButton = new Button(container, SWT.NONE);
		testSourceFolderButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleFolderSelection(testSourceFolder);
			}
		});
		testSourceFolderButton.setText("Browse...");

		lblTestLibraryFolder = new Label(container, SWT.NONE);
		lblTestLibraryFolder.setText("&Test library folder:");

		testLibraryFolder = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd_testLibrayFolder = new GridData(GridData.FILL_HORIZONTAL);
		testLibraryFolder.setLayoutData(gd_testLibrayFolder);
		testLibraryFolder.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button testLibraryFolderButton = new Button(container, SWT.NONE);
		testLibraryFolderButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleFolderSelection(testLibraryFolder);
			}
		});
		testLibraryFolderButton.setText("Browse...");

		Label label = new Label(parentContainer, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1,
				1));

		Label lblWorkspaceJstestdriverSettings = new Label(parentContainer,
				SWT.NONE);
		lblWorkspaceJstestdriverSettings.setBounds(0, 0, 59, 14);
		lblWorkspaceJstestdriverSettings
				.setText("Workspace JsTestDriver settings");

		Composite container2 = new Composite(parentContainer, SWT.NONE);
		container2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false,
				false, 1, 1));
		GridLayout layout2 = new GridLayout();
		container2.setLayout(layout2);
		layout2.numColumns = 3;
		layout2.verticalSpacing = 9;
		Label lblPort = new Label(container2, SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false,
				1, 1));
		lblPort.setText("&Port to start server on:");

		port = new Text(container2, SWT.BORDER);
		port.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container2, SWT.NONE);

		lblpathToIE = new Label(container2, SWT.NONE);
		lblpathToIE.setText("&Path to IE:");

		pathToIE = new Text(container2, SWT.BORDER);
		pathToIE.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		pathToIE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				1, 1));

		Button pathToIEButton = new Button(container2, SWT.NONE);
		pathToIEButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowserSelection(pathToIE);
			}
		});
		pathToIEButton.setText("Browse...");

		lblpathToFirefox = new Label(container2, SWT.NONE);
		lblpathToFirefox.setText("&Path to FireFox:");

		pathToFireFox = new Text(container2, SWT.BORDER);
		pathToFireFox.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		pathToFireFox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Button pathToFireFoxButton = new Button(container2, SWT.NONE);
		pathToFireFoxButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowserSelection(pathToFireFox);
			}
		});
		pathToFireFoxButton.setText("Browse...");

		lblpathToChrome = new Label(container2, SWT.NONE);
		lblpathToChrome.setText("&Path to Chrome:");

		pathToChrome = new Text(container2, SWT.BORDER);
		pathToChrome.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		pathToChrome.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Button pathToChromeButton = new Button(container2, SWT.NONE);
		pathToChromeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowserSelection(pathToChrome);
			}
		});
		pathToChromeButton.setText("Browse...");

		lblpathToOpera = new Label(container2, SWT.NONE);
		lblpathToOpera.setText("&Path to Opera:");

		pathToOpera = new Text(container2, SWT.BORDER);
		pathToOpera.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		pathToOpera.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Button pathToOperaButton = new Button(container2, SWT.NONE);
		pathToOperaButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowserSelection(pathToOpera);
			}
		});
		pathToOperaButton.setText("Browse...");

		lblpathToSafari = new Label(container2, SWT.NONE);
		lblpathToSafari.setText("&Path to Safari:");

		pathToSafari = new Text(container2, SWT.BORDER);
		pathToSafari.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		pathToSafari.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));

		Button pathToSafariButton = new Button(container2, SWT.NONE);
		pathToSafariButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowserSelection(pathToSafari);
			}
		});
		pathToSafariButton.setText("Browse...");

		initialize();
		dialogChanged();
		setControl(parentContainer);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (obj instanceof IResource) {
					IProject project;
					if (obj instanceof IProject)
						project = (IProject) obj;
					else
						project = ((IResource) obj).getParent().getProject();
					IScopeContext projectScope = new ProjectScope(project);
					IEclipsePreferences projectNode = projectScope
							.getNode(Activator.getDefault().getBundle()
									.getSymbolicName());
					if (projectNode != null) {
						sourceFolder.setText(projectNode.get(
								Constants.SOURCE_FOLDER_ID, dirSeparator
										+ project.getName()
										+ defaultSourceFolder));
						testSourceFolder.setText(projectNode.get(
								Constants.TEST_SOURCE_FOLDER_ID, dirSeparator
										+ project.getName()
										+ defaultTestSourceFolder));
						testLibraryFolder.setText(projectNode.get(
								Constants.TEST_LIBRARY_FOLDER_ID, dirSeparator
										+ project.getName()
										+ defaultTestLibraryFolder));
					}
//					IPreferenceStore preference = Activator.getDefault()
//							.getPreferenceStore();
//					String sourceFolderName = preference
//							.getString(Constants.SOURCE_FOLDER_ID);
//					sourceFolder
//							.setText(BLANK.equals(sourceFolderName) ? dirSeparator
//									+ project.getName() + defaultSourceFolder
//									: sourceFolderName);
//					String testSourceFolderName = preference
//							.getString(Constants.TEST_SOURCE_FOLDER_ID);
//					testSourceFolder
//							.setText(BLANK.equals(testSourceFolderName) ? dirSeparator
//									+ project.getName()
//									+ defaultTestSourceFolder
//									: testSourceFolderName);
//					String testLibraryFolderName = preference
//							.getString(Constants.TEST_LIBRARY_FOLDER_ID);
//					testLibraryFolder.setText(BLANK
//							.equals(testLibraryFolderName) ? dirSeparator
//							+ project.getName() + defaultTestLibraryFolder
//							: testLibraryFolderName);
				}
			}
		}

		IPreferenceStore preferenceStore = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, JsTestDriverConfigConstants.PLUGIN_ID);
		port.setText(preferenceStore
				.getString(JsTestDriverConfigConstants.PREFERRED_SERVER_PORT));
		pathToIE.setText(preferenceStore
				.getString(JsTestDriverConfigConstants.IE_PATH));
		pathToFireFox.setText(preferenceStore
				.getString(JsTestDriverConfigConstants.FIREFOX_PATH));
		pathToChrome.setText(preferenceStore
				.getString(JsTestDriverConfigConstants.CHROME_PATH));
		pathToOpera.setText(preferenceStore
				.getString(JsTestDriverConfigConstants.OPERA_PATH));
		pathToSafari.setText(preferenceStore
				.getString(JsTestDriverConfigConstants.SAFARI_PATH));
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleFolderSelection(Text target) {
		IResource currentResource = root.findMember(new Path(target.getText()));
		FolderSelectionDialog dialog = new FolderSelectionDialog(getShell(),
				new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setInput(root);
		dialog.setTitle("Folder selection");
		dialog.setMessage("Select folder");
		dialog.setInitialSelection(currentResource);
		dialog.setAllowMultiple(false);
		if (dialog.open() == FolderSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				target.setText(((IContainer) result[0]).getFullPath()
						.toString());
			}
		}
	}

	private void handleBrowserSelection(Text text) {
		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SHEET);
		if (text.getText() != null) {
			dialog.setFileName(text.getText());
		}
		String file = dialog.open();
		if (file != null) {
			file = file.trim();
			if (file.length() > 0) {
				text.setText(file.toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		if (sourceFolder.getText().length() == 0) {
			updateStatus("Source folder must be specified");
			return;
		}
		if (testSourceFolder.getText().length() == 0) {
			updateStatus("Test source folder must be specified");
			return;
		}
		if (testLibraryFolder.getText().length() == 0) {
			updateStatus("Test library folder must be specified");
			return;
		}

		String portString = port.getText();
		if (portString.length() == 0) {
			updateStatus("Port must be an Integer");
			return;
		}
		try {
			int number = Integer.valueOf(portString).intValue();
			if (number < 0 && number > Integer.MAX_VALUE) {
				updateStatus("Port must be an Integer");
				return;
			}
		} catch (NumberFormatException e) {
			updateStatus("Port must be an Integer");
			return;
		}

		if (pathToIE.getText().length() != 0
				&& !new File(pathToIE.getText()).exists()) {
			updateStatus("Path to IE" + " must be an existing file");
			return;
		}
		if (pathToFireFox.getText().length() != 0
				&& !new File(pathToFireFox.getText()).exists()) {
			updateStatus("Path to FireFox" + " must be an existing file");
			return;
		}
		if (pathToChrome.getText().length() != 0
				&& !new File(pathToChrome.getText()).exists()) {
			updateStatus("Path to Chrome" + " must be an existing file");
			return;
		}
		if (pathToOpera.getText().length() != 0
				&& !new File(pathToOpera.getText()).exists()) {
			updateStatus("Path to Opera" + " must be an existing file");
			return;
		}
		if (pathToSafari.getText().length() != 0
				&& !new File(pathToSafari.getText()).exists()) {
			updateStatus("Path to Safari" + " must be an existing file");
			return;
		}
		IProject srcPrj;
		try {
			srcPrj = root.getProject(new Path(sourceFolder.getText())
					.segment(FIRST_SEGMENT));
		} catch (IllegalArgumentException e) {
			updateStatus("Source folder must be specified with a valid project name");
			return;
		}
		if (!srcPrj.exists()) {
			updateStatus("Source folder must be specified with an exsisting project name");
			return;
		}
		IProject testSrcPrj;
		try {
			testSrcPrj = root.getProject(new Path(testSourceFolder.getText())
					.segment(FIRST_SEGMENT));
		} catch (IllegalArgumentException e) {
			updateStatus("Test source folder must be specified with a valid project name");
			return;
		}
		if (!testSrcPrj.exists()) {
			updateStatus("Test Source folder must be specified with an exsisting project name");
			return;
		}
		IProject testLibPrj;
		try {
			testLibPrj = root.getProject(new Path(testLibraryFolder.getText())
					.segment(FIRST_SEGMENT));
		} catch (IllegalArgumentException e) {
			updateStatus("Test library folder must be specified with a valid project name");
			return;
		}
		if (!testLibPrj.exists()) {
			updateStatus("Test library folder must be specified with an exsisting project name");
			return;
		}
		if (!testSrcPrj.getName().equals(testLibPrj.getName())) {
			updateStatus("Test source folder and Test library folder must be within a same project");
			return;
		}
		IResource sourceFolderResource = root.findMember(new Path(sourceFolder
				.getText()));
		if (sourceFolderResource instanceof IFile) {
			updateStatus("Source folder has an existing file");
			return;
		}
		IResource testSourceFolderResource = root.findMember(new Path(
				testSourceFolder.getText()));
		if (testSourceFolderResource instanceof IFile) {
			updateStatus("Test source folder has an existing file");
			return;
		}
		IResource testLibraryFolderResource = root.findMember(new Path(
				testLibraryFolder.getText()));
		if (testLibraryFolderResource instanceof IFile) {
			updateStatus("Test library folder has an existing file");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public Text getSourceFolder() {
		return sourceFolder;
	}

	public Text getTestSourceFolder() {
		return testSourceFolder;
	}

	public Text getTestLibraryFolder() {
		return testLibraryFolder;
	}

	public Text getPort() {
		return port;
	}

	public Text getPathToIE() {
		return pathToIE;
	}

	public Text getPathToFireFox() {
		return pathToFireFox;
	}

	public Text getPathToChrome() {
		return pathToChrome;
	}

	public Text getPathToOpera() {
		return pathToOpera;
	}

	public Text getPathToSafari() {
		return pathToSafari;
	}

}