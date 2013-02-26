package org.isdefe.wicket.anchorbrowserhistory;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnLoadHeaderItem;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;

/**
 * Behaviour that can be added to a page to recieve event from history token change
 * Also, cand send token changes to page (with or without firing the event )
 *
 *
 * User: obaisdefe
 * Date: 13-feb-2013
 */
public abstract class HistoryAjaxBehaviour extends AbstractDefaultAjaxBehavior {

    private static final String PARAM_TOKEN = "TOKEN";



    /**
     * Change the url token.
     * By default, it does not trigger the event
     *
     * @param token  the String to set the token to
     * @param target the AjaxRequestTarget that triggered this change
     */
    public static void changeToken(String token, AjaxRequestTarget target) {
        changeToken(token, target, false);
    }

    /**
     * Change the url token.
     *
     * @param token        the String to set the token to
     * @param target       the AjaxRequestTarget that triggered this change
     * @param triggerEvent set to false if you don't want the listeners to get the event
     *                     when the token is changed
     */
    public static void changeToken(String token, AjaxRequestTarget target, boolean triggerEvent) {
        target.appendJavaScript(getScript(token,triggerEvent));
    }


    /**
     * Returns the javascript to execute a change of url tolen
     * By default, it does not trigger the event
     * @param token  the String to set the token to
     * @return Javascript code
     */
    public static String getScript(String token) {
        return getScript(token,false);
    }

    /**
     * Returns the javascript to execute a change of url tolen
     * By default, it does not trigger the event
     * @param token  the String to set the token to
     * @param triggerEvent set to false if you don't want the listeners to get the event
     *                     when the token is changed
     * @return Javascript code
     */
    public static String getScript(String token,  boolean triggerEvent) {
        StringBuilder sbjs = new StringBuilder();
        if (!triggerEvent) {
            sbjs.append("tokenManagerSkipEvent = true;\n");
        }
        sbjs.append("window.location.hash = '").append(JavaScriptUtils.escapeQuotes(token)).append("';\n");
        return sbjs.toString();

    }

    // If initial token is launched back to this behaviour
    private boolean getTokenOnStartup = false;

    /**
     * Default constructor. Will not trigger the event as soon as the page
     * loads. Only when the token is changed.
     */
    public HistoryAjaxBehaviour() {
        super();
    }

    /**
     * Constructor.
     *
     * @param getTokenOnStartup if true, an ajax request will be sent to get the token as soon
     *                          as the page loads. Default to false.
     */
    public HistoryAjaxBehaviour(boolean getTokenOnStartup) {
        this();
        this.getTokenOnStartup = getTokenOnStartup;
    }

    @Override
    protected void respond(AjaxRequestTarget target) {
        // Recover token value
        IRequestParameters requestParameters = RequestCycle.get().getRequest().getRequestParameters();
        org.apache.wicket.util.string.StringValue tokenParameterValue = requestParameters.getParameterValue(PARAM_TOKEN);
        String token = tokenParameterValue.toString();
        onTokenChanged(target, token);
        //... if you want to send a wicket event, use it on abstract method
        //getComponent().send(getComponent().getPage(), Broadcast.BREADTH,new TokenChangedEvent(target, tokenParameterValue.toString()));
        // - - - -
        // ..or if you want to add data to the ajaxtargetrequest and use the same ajax event
        // RequestCycle.get().setMetaData(new MetaDataKey<String>(),token);
        //
    }

    /**
     * Method called when the token changes.
     *
     *
     * @param ajaxRequestTarget the {@link AjaxRequestTarget}
     * @param token  the url token (can be empty)
     */
    public abstract void onTokenChanged(AjaxRequestTarget ajaxRequestTarget, String token);


    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        StringBuilder sbOnLoadJavaScript;
        sbOnLoadJavaScript = new StringBuilder();
        // Set the variable to skip events
        sbOnLoadJavaScript.append("var tokenManagerSkipEvent = false;").append("\n");
        // Set method to call when hash changes
        sbOnLoadJavaScript.append("var HistoricoTokenAjaxBehaviour = ").append(getCallbackFunction(CallbackParameter.explicit(PARAM_TOKEN)));
        response.render(JavaScriptHeaderItem.forScript(sbOnLoadJavaScript.toString(), HistoryAjaxBehaviour.class.getName()));
        sbOnLoadJavaScript = new StringBuilder();
        // On page load, bind to event (thanks jQuery ! )
        sbOnLoadJavaScript.append("$(window).bind('hashchange', function() {").append("\n");
        sbOnLoadJavaScript.append("    if (tokenManagerSkipEvent){").append("\n");
        sbOnLoadJavaScript.append("        tokenManagerSkipEvent = false;").append("\n");
        sbOnLoadJavaScript.append("    } else{").append("\n");
        sbOnLoadJavaScript.append("        HistoricoTokenAjaxBehaviour(window.location.hash.replace('#', ''));").append("\n");
        sbOnLoadJavaScript.append("    } ").append("\n");
        sbOnLoadJavaScript.append("});").append("\n");
        // Launch and event on startup ?
        if (getTokenOnStartup) {
            sbOnLoadJavaScript.append("HistoricoTokenAjaxBehaviour(window.location.hash);").append("\n");
        }
        response.render(OnLoadHeaderItem.forScript(sbOnLoadJavaScript.toString())); // Onload


    }





}
