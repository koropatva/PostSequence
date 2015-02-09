package com.dron.sender.controllers.root.strategies;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.dron.sender.config.AppProperties;
import com.dron.sender.controllers.base.interfaces.IBaseController;
import com.dron.sender.controllers.root.ModelRootController;
import com.dron.sender.controllers.root.RootController;
import com.dron.sender.controllers.root.controls.AutoFillSequenceHelper;
import com.dron.sender.controllers.root.controls.ParamTableView;
import com.dron.sender.controllers.root.controls.RootConfig;
import com.dron.sender.pattern.interfaces.IControllerStrategy;
import com.dron.sender.pattern.models.strategy.ControllerActionStrategy;
import com.dron.sender.pattern.services.strategies.ControllerStrategyContext;

@Component
public class InitializeStrategy extends ModelRootController implements
		IControllerStrategy {

	@Resource
	private ApplicationContext ctx;

	@Autowired
	private ControllerStrategyContext context;

	@Autowired
	private AppProperties appProperties;

	@Override
	public ControllerActionStrategy getStrategy() {
		return ControllerActionStrategy.ROOT_INITIALIZE;
	}

	@Override
	public void execute(IBaseController iBaseController) {
		RootController controller = (RootController) iBaseController;
		setUp(controller);

		uiSequence.clear();
		uiSequence.prepareEmptySequence();

		tfNewPluginName.textProperty().bindBidirectional(uiSequence.getName());

		btnSendSequence.disableProperty().bind(RootConfig.getDisableRootProperty());
		btnSendActivePlugin.disableProperty().bind(RootConfig.getDisableRootProperty());

		tfUrl.disableProperty().bind(RootConfig.getDisableRootProperty());
		tfUrl.textProperty().addListener((observer, oldValue, newValue) -> {
			uiSequence.getSelectedUIPLugin().setUrl(newValue);
		});

		cbMethods.disableProperty().bind(RootConfig.getDisableRootProperty());
		cbMethods.getItems().addAll(HttpMethod.GET.name(),
				HttpMethod.POST.name(), HttpMethod.PUT.name(),
				HttpMethod.DELETE.name());
		cbMethods.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					cbMethods.getSelectionModel().select(newValue);
					uiSequence.getSelectedUIPLugin().setMethod(newValue);

					RootConfig.bindPostBody(txaPostBody, newValue);

					updateControls();
				});

		tblParams = new ParamTableView().initialize(uiSequence.getUIParams(),
				tblParams);
		tblParams
				.editableProperty()
				.addListener(
						(observer, oldValue, newValue) -> {
							context.execute(
									controller,
									ControllerActionStrategy.ROOT_FILL_UI_PLUGIN_ACCORDION);
						});

		txaPostBody.managedProperty().bind(txaPostBody.visibleProperty());
		txaPostBody.disableProperty().bind(RootConfig.getDisableRootProperty());
		txaPostBody.textProperty().addListener(
				(observer, oldValue, newValue) -> {
					uiSequence.getSelectedUIPLugin().setPostBody(newValue);
				});

		tbtnHeaders.setSelected(true);
		tbtnHeaders.disableProperty().bind(RootConfig.getDisableRootProperty());
		tbtnHeaders.setOnAction(event -> {
			tblHeaders.setVisible(!tblHeaders.isVisible());
			RootConfig.bindHeaders(tblHeaders.isVisible()
					|| tblParams.isVisible());

			updateControls();
		});

		tbtnParams.setSelected(true);
		tbtnParams.setOnAction(event -> {
			tblParams.setVisible(!tblParams.isVisible());

			RootConfig.bindHeaders(tblHeaders.isVisible()
					|| tblParams.isVisible());

			updateControls();
		});

		tblHeaders.disableProperty().bind(RootConfig.getDisableRootProperty());

		autoFillSequenceTextBox.setUp(ctx, controller);
		autoFillSequenceTextBox.setItems(AutoFillSequenceHelper
				.getFiles(appProperties.getFilePath()));
		
		context.execute(controller,
				ControllerActionStrategy.ROOT_NEW_UI_SEQUENCE);

		context.execute(controller,
				ControllerActionStrategy.ROOT_FILL_ROOT_CONTROLS);
	}
}