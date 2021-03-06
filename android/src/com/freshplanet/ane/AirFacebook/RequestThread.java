//////////////////////////////////////////////////////////////////////////////////////
//
//  Copyright 2012 Freshplanet (http://freshplanet.com | opensource@freshplanet.com)
//  
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//  
//    http://www.apache.org/licenses/LICENSE-2.0
//  
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//  
//////////////////////////////////////////////////////////////////////////////////////

package com.freshplanet.ane.AirFacebook;

import android.os.Bundle;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

public class RequestThread extends Thread
{
	private AirFacebookExtensionContext _context;
	
	private String _graphPath;
	private Bundle _parameters;
	private String _httpMethod;
	private String _callback;
	
	public RequestThread(AirFacebookExtensionContext context, String graphPath, Bundle parameters, String httpMethod, String callback)
	{
		_context = context;
		_graphPath = graphPath;
		_parameters = parameters;
		_httpMethod = httpMethod;
		_callback = callback;
	}
	
    @Override
    public void run()
    {
    	Session session = _context.getSession();
    	
    	String data = null;
		String error = null;
		try
		{
			Request request;
			if (_parameters != null)
			{
				request = new Request(session, _graphPath, _parameters, HttpMethod.valueOf(_httpMethod));
			}
			else
			{
				request = new Request(session, _graphPath);
			}
			
			Response response = request.executeAndWait();
			if (response.getGraphObject() != null)
			{
				data = response.getGraphObject().getInnerJSONObject().toString();
			}
			else if (response.getGraphObjectList() != null)
			{
				data = response.getGraphObjectList().getInnerJSONArray().toString();
			}
			else if (response.getError() != null)
			{
				data = response.getError().getRequestResult().toString();
			}
		}
		catch (Exception e)
		{
			error = e.getMessage();
		}
		
		String result = "";
		if (error != null) result = error;
		else if (data != null) result = data;
		
		if (_callback != null)
		{
			_context.dispatchStatusEventAsync(_callback, result);
		}
    }	
}