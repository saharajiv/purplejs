package io.purplejs.boot;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.google.common.io.Files;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import io.purplejs.boot.impl.AssetFilter;
import io.purplejs.boot.impl.BannerPrinter;
import io.purplejs.servlet.ScriptServlet;

public final class MainApp
{
    private final String[] args;

    public MainApp( final String... args )
    {
        this.args = args;
    }

    public void start()
        throws Exception
    {
        new BannerPrinter().printBanner();

        final ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );

        context.addFilter( AssetFilter.class, "/*", EnumSet.of( DispatcherType.REQUEST ) );

        final ServletHolder servlet = context.addServlet( ScriptServlet.class, "/*" );
        servlet.setInitParameter( "resource", "/app/main.js" );
        servlet.setInitParameter( "devSourceDirs", System.getProperty( "io.purplejs.devSourceDirs" ) );

        final String location = Files.createTempDir().getAbsolutePath();
        final MultipartConfigElement multipartConfig = new MultipartConfigElement( location );
        servlet.getRegistration().setMultipartConfig( multipartConfig );

        final HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler( context );

        final Server server = new Server( 8080 );
        server.setHandler( handlers );
        server.start();
    }

    public static void main( final String... args )
        throws Exception
    {
        new MainApp( args ).start();
    }
}