package project.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import project.view.SimpleZestView;

public class UpdateGraphHandler {
	private static final String SIMPLEZESTVIEW = "project-ex-1014-simple-zest-mezzell.partdescriptor.simplezestview";

	@Execute
	public void execute(EPartService epartService) {
		MPart findPart = epartService.findPart(SIMPLEZESTVIEW);

		if (findPart != null) {
			Object findPartObj = findPart.getObject();
			if (findPartObj instanceof SimpleZestView) {
				SimpleZestView viewPart = (SimpleZestView) findPartObj;
				viewPart.updateGraphFromFile();
			}
		}
	}
}
