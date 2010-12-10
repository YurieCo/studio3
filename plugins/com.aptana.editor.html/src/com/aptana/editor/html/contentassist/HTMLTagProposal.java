package com.aptana.editor.html.contentassist;

import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;

import com.aptana.editor.common.CommonEditorPlugin;
import com.aptana.editor.common.contentassist.CommonCompletionProposal;
import com.aptana.editor.common.contentassist.UserAgentManager;
import com.aptana.editor.html.contentassist.index.HTMLIndexConstants;
import com.aptana.editor.html.contentassist.model.ElementElement;

class HTMLTagProposal extends CommonCompletionProposal
{

	private Integer[] _positions;

	/**
	 * positions must have at least one position. The first is cursor position, the last is linked mode exit position.
	 * Any in between are tabstops in linked mode UI.
	 * 
	 * @param replacementString
	 * @param replacementOffset
	 * @param replacementLength
	 * @param element
	 * @param positions
	 */
	HTMLTagProposal(String replacementString, int replacementOffset, int replacementLength,
			ElementElement element, Integer... positions)
	{
		super(replacementString, replacementOffset, replacementLength, positions[0],
				HTMLContentAssistProcessor.ELEMENT_ICON, element.getName(), null, element.getDescription());
		setFileLocation(HTMLIndexConstants.CORE);

		List<String> userAgents = element.getUserAgentNames();
		Image[] userAgentIcons = UserAgentManager.getInstance().getUserAgentImages(userAgents);
		setUserAgentImages(userAgentIcons);

		this._positions = positions;
	}

	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset)
	{
		super.apply(viewer, trigger, stateMask, offset);

		// See if there are any positions that should be linked. Last is always exit, first is cursor position
		if (_positions != null && _positions.length > 0)
		{
			IDocument document = viewer.getDocument();
			boolean validPrefix = isValidPrefix(getPrefix(document, offset), getDisplayString());
			int shift = (validPrefix) ? offset - this._replacementOffset : 0;

			try
			{
				LinkedModeModel.closeAllModels(document); // Exit out of any existing linked mode

				LinkedModeModel model = new LinkedModeModel();
				int i = 0;
				for (int pos : _positions)
				{
					LinkedPositionGroup group = new LinkedPositionGroup();
					group.addPosition(new LinkedPosition(document, (offset - shift) + pos, 0, i++));
					model.addGroup(group);
				}

				model.forceInstall();
				LinkedModeUI ui = new LinkedModeUI(model, viewer);
				// When user hits Enter/Return we should actually insert it (and exit?)!
				ui.setExitPolicy(new IExitPolicy()
				{

					public ExitFlags doExit(LinkedModeModel model, VerifyEvent event, int offset, int length)
					{
						if (event.character == '\n' || event.character == '\r')
						{
							return new ExitFlags(ILinkedModeListener.EXIT_ALL, true);
						}
						return null;
					}
				});

				ui.setCyclingMode(LinkedModeUI.CYCLE_ALWAYS);
				ui.setExitPosition(viewer, (offset - shift) + _positions[_positions.length - 1], 0, Integer.MAX_VALUE);
				ui.enter();
			}
			catch (BadLocationException e)
			{
				CommonEditorPlugin.logError(e);
			}
		}
	}

}
