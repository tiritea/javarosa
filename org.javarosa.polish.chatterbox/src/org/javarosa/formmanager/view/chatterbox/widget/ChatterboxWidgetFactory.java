package org.javarosa.formmanager.view.chatterbox.widget;

import org.javarosa.core.model.Constants;
import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.QuestionDef;
import org.javarosa.core.model.utils.PrototypeFactory;
import org.javarosa.formmanager.view.chatterbox.Chatterbox;

import de.enough.polish.ui.ChoiceGroup;

public class ChatterboxWidgetFactory {
	Chatterbox cbox;
	
	PrototypeFactory widgetFactory;
	
	public ChatterboxWidgetFactory (Chatterbox cbox) {
		widgetFactory = new PrototypeFactory();
		this.cbox = cbox;
	}
	
	public void registerExtendedWidget(int controlType, IWidgetStyle prototype) {
		widgetFactory.addNewPrototype(String.valueOf(controlType), prototype.getClass());
	}
	
	public ChatterboxWidget getWidget (QuestionDef question, FormDef form, int initViewState) {
		IWidgetStyle collapsedStyle = null;
		IWidgetStyleEditable expandedStyle = null;
		
		int controlType = question.getControlType();
		int dataType = question.getDataType();
		String appearanceAttr = question.getAppearanceAttr();
		
		collapsedStyle = new CollapsedWidget();
		
		switch (controlType) {
		case Constants.CONTROL_INPUT:
			switch (dataType) {
			case Constants.DATATYPE_INTEGER:
				expandedStyle = new NumericEntryWidget();
				break;
			case Constants.DATATYPE_DATE:
				expandedStyle = new DateEntryWidget();
				break;
			}
			break;
		case Constants.CONTROL_SELECT_ONE:
			int style;

			if ("minimal".equals(appearanceAttr))
				style = ChoiceGroup.POPUP;
			else
				style = ChoiceGroup.EXCLUSIVE;

			expandedStyle = new SelectOneEntryWidget(style);
			break;
		case Constants.CONTROL_SELECT_MULTI:
			expandedStyle = new SelectMultiEntryWidget();
			break;
		case Constants.CONTROL_TEXTAREA:
			expandedStyle = new TextEntryWidget();
			break;
		}

		if (expandedStyle == null) {
			expandedStyle = new TextEntryWidget();
			String name = String.valueOf(controlType);
			try {
				Object widget;
				widget = widgetFactory.getNewInstance(name);

				if (widget == null) {
				} else {
					expandedStyle = (IWidgetStyleEditable) widget;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
		
		if (collapsedStyle == null || expandedStyle == null)
			throw new IllegalStateException("No appropriate widget to render question");
		
		return new ChatterboxWidget(cbox, question, form, initViewState, collapsedStyle, expandedStyle);
	}
}
