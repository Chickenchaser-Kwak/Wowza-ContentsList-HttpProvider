package com.chasersgroup.chickenchaser.wowza;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;

import com.wowza.wms.application.Application;
import com.wowza.wms.http.HTTPServerVersion;
import com.wowza.wms.http.HTTProvider2Base;
import com.wowza.wms.http.IHTTPRequest;
import com.wowza.wms.http.IHTTPResponse;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.vhost.IVHost;

public class ContentsListHTTPProvider extends HTTProvider2Base {
	
	private class Temp
	{
		public String path;
		public long lastModified;
		public Temp(String path, long lastModified) {
			super();
			this.path = path;
			this.lastModified = lastModified;
			if(path==null)
				path = "";
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();

			builder.append("{");
			builder.append("\"path\":").append("\"").append(path).append("\"").append(",");
			builder.append("\"lastModified\":"+lastModified).append("");
			builder.append("}");
			
			return builder.toString();
		}
	}

	public void onHTTPRequest(IVHost vhost, IHTTPRequest req, IHTTPResponse resp) {

		String streamKeyQuery = null;
		try {
			String[] quries = req.getQueryString().split("&");
			for(String query : quries )
			{
				if( query.startsWith("streamkey")) {
					streamKeyQuery = query.substring(query.indexOf('=')+1);
				}
			}

		} catch(Exception ex) {
			ex.printStackTrace();
		}

		StringBuffer strBuffer = new StringBuffer();

		try {
			String storagePath = new Application("", vhost).getAppInstance("").getStreamStorageDir();

			File storage = new File(storagePath);
			if( storage.exists() && storage.isDirectory() )
			{
				File[] all = storage.listFiles();
									
				ArrayList<Temp> founded = new ArrayList<>();
				for(File file : all )
				{
					if( file.isHidden() || file.isDirectory() || !file.toString().endsWith(".mp4") )
						continue;

					// regularize contents path
					String path = file.toString();
					path = path.replace('\\', '/');	// file seperator : windows -> linux
					
					if( !path.contains(storagePath) )
						continue;

					path = path.replace(storagePath, "");
					path = path.replace(".mp4", "");
					while(path.startsWith("/"))
						path = path.substring(1, path.length());
					// regularize contents path end
					
					//
					if( streamKeyQuery == null )
					{
						founded.add(new Temp(path, file.lastModified()));
					}
					else if( streamKeyQuery.isEmpty() )
					{
						;
					}
					else if( path.startsWith(streamKeyQuery) )
					{
						founded.add(new Temp(path, file.lastModified()));
					}
				}

				strBuffer.append( founded );
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}


		try
		{
			resp.setHeader("Content-Type", "application/json");

			OutputStream out = resp.getOutputStream();
			byte[] outBytes = strBuffer.toString().getBytes();
			out.write(outBytes);
		}
		catch (Exception e)
		{
			WMSLoggerFactory.getLogger(HTTPServerVersion.class).error("HTTPProviderStreamReset.onHTTPRequest: "+e.toString());
			e.printStackTrace();
		}

	}

}

