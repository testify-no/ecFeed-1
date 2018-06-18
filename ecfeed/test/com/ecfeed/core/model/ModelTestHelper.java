package com.ecfeed.core.model;

import java.io.ByteArrayInputStream;

import com.ecfeed.core.serialization.IModelParser;
import com.ecfeed.core.serialization.ParserException;
import com.ecfeed.core.serialization.ect.EctParser;
import com.ecfeed.core.utils.ExceptionHelper;

public class ModelTestHelper {

	public static RootNode createModel(String modelXml) {

		try {
			IModelParser parser = new EctParser();
			ByteArrayInputStream istream = new ByteArrayInputStream(modelXml.getBytes());

			return parser.parseModel(istream);

		} catch(ParserException e) {
			ExceptionHelper.reportRuntimeException(e.getMessage());
		}

		return null;
	}

}
