package com.dron.sender.controllers.root.strategies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.dron.sender.controllers.base.interfaces.IBaseController;
import com.dron.sender.controllers.root.RootController;
import com.dron.sender.controllers.root.models.BaseRootController;
import com.dron.sender.controllers.root.tasks.SequenceTask;
import com.dron.sender.pattern.interfaces.IControllerStrategy;
import com.dron.sender.pattern.models.strategy.ControllerActionStrategy;
import com.dron.sender.pattern.models.transformers.TransformKey;
import com.dron.sender.pattern.services.observers.BaseFormatLoggerObserver;
import com.dron.sender.pattern.services.observers.BaseLoggerObserver;
import com.dron.sender.pattern.services.strategies.ControllerStrategyContext;
import com.dron.sender.pattern.services.transformers.TransformerFactory;
import com.dron.sender.sequence.models.Param;
import com.dron.sender.sequence.models.Plugin;
import com.dron.sender.sequence.models.Sequence;

@Component
public class SendSequenceStrategy extends BaseRootController implements
		IControllerStrategy {

	@Autowired
	private ControllerStrategyContext context;

	@Autowired
	private ApplicationContext ctx;

	@Override
	public ControllerActionStrategy getStrategy() {
		return ControllerActionStrategy.ROOT_SEND_SEQUENCE;
	}

	@Override
	public void execute(IBaseController iBaseController) {
		RootController controller = (RootController) iBaseController;
		setUp(controller);

		Sequence sequence = fillRootSequence();
		initLogging(sequence);

		txaResponce.setText("");
		context.execute(controller,
				ControllerActionStrategy.ROOT_DISABLE_CONTROLS);

		SequenceTask sequenceTask = new SequenceTask(sequence, ctx);

		new Thread(sequenceTask).start();
	}

	private Sequence fillRootSequence() {
		Sequence sequence = new Sequence();
		TransformerFactory.reverseTransformEntity(sequence, uiSequence,
				TransformKey.ROOT_SEQUENCE);
		return sequence;
	}

	private void initLogging(Sequence sequence) {
		for (Plugin plugin : sequence.getPlugins()) {
			new BaseLoggerObserver(plugin, txaResponce,
					Plugin.PROPERTY_RESPONCE);
			new BaseFormatLoggerObserver(plugin, txaConsole,
					Plugin.PROPERTY_RESPONCE);
		}

		for (Param param : sequence.getParams()) {
			new BaseLoggerObserver(param, txaResponce);
		}
	}

}
