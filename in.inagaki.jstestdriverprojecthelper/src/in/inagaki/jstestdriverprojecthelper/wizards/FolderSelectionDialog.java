package in.inagaki.jstestdriverprojecthelper.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.NewFolderDialog;

public class FolderSelectionDialog extends ElementTreeSelectionDialog implements
		ISelectionChangedListener {

	private Button button;

	public FolderSelectionDialog(Shell parent, ILabelProvider labelProvider,
			ITreeContentProvider contentProvider) {
		super(parent, labelProvider, contentProvider);
		addFilter(new FolderViewerFilter());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite result = (Composite) super.createDialogArea(parent);

		getTreeViewer().addSelectionChangedListener(this);

		button = new Button(result, SWT.PUSH);
		button.setText("New folder...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newFolderButtonPressed();
			}
		});
		button.setFont(parent.getFont());

		IStructuredSelection selection = (IStructuredSelection) getTreeViewer()
				.getSelection();
		if (selection.isEmpty()) {
			button.setEnabled(false);
		}
		return result;

	}

	protected void newFolderButtonPressed() {
		IStructuredSelection selection = (IStructuredSelection) getTreeViewer()
				.getSelection();
		IContainer selectedContainer = (IContainer) selection.getFirstElement();
		NewFolderDialog dialog = new NewFolderDialog(getShell(),
				selectedContainer);
		if (dialog.open() == Window.OK) {
			TreeViewer treeViewer = getTreeViewer();
			treeViewer.refresh(selectedContainer);
			Object createdFolder = dialog.getResult()[0];
			treeViewer.reveal(createdFolder);
			treeViewer.setSelection(new StructuredSelection(createdFolder));
		}

	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		IStructuredSelection selection = (IStructuredSelection) getTreeViewer()
				.getSelection();
		button.setEnabled(selection.isEmpty() ? false : true);

	}

}
