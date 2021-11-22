package org.apache.logging.log4j.core.layout;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.handler.LevelErrorhandler;
import org.apache.logging.log4j.core.handler.LevelInfoHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Plugin(name = "CustomerJsonLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE, printObject = true)
public final class CustomerJsonLayout extends AbstractStringLayout {

    static final String CONTENT_TYPE = "application/json";
    private final LevelErrorhandler errorhandler = new LevelErrorhandler();
    private final LevelInfoHandler infoHandler = new LevelInfoHandler();

    private CustomerJsonLayout(final Configuration config, final Charset charset,
                               final Serializer headerSerializer, final Serializer footerSerializer) {
        super(config, charset, headerSerializer, footerSerializer);
    }

    @PluginBuilderFactory
    public static <B extends CustomerJsonLayout.Builder<B>> B newBuilder() {
        return new CustomerJsonLayout.Builder<B>().asBuilder();
    }

    @Override
    public String toSerializable(LogEvent event) {

        switch (event.getLevel().name()) {
            case "OFF": {
                return "";
            }
            case "ERROR": {
                return errorhandler.handle(event);
            }
            default: {
                return infoHandler.handle(event);
            }
        }
    }

    @Override
    public byte[] getHeader() {
        return null;
    }

    @Override
    public byte[] getFooter() {
        return null;
    }

    @Override
    public Map<String, String> getContentFormat() {
        final Map<String, String> result = new HashMap<>();
        result.put("version", "2.0");
        return result;
    }

    @Override
    public String getContentType() {
        return CONTENT_TYPE + "; charset=" + this.getCharset();
    }

    public static class Builder<B extends CustomerJsonLayout.Builder<B>> extends AbstractStringLayout.Builder<B>
            implements org.apache.logging.log4j.core.util.Builder<CustomerJsonLayout> {


        public Builder() {
            super();
            setCharset(StandardCharsets.UTF_8);
        }

        @Override
        public CustomerJsonLayout build() {
            if (super.getCharset() == null) super.setCharset(StandardCharsets.UTF_8);
            Configuration configuration = getConfiguration();
            return new CustomerJsonLayout(configuration,
                    super.getCharset(),
                    null, null);
        }
    }
}
