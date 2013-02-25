package org.isdefe.wicket.anchorbrowserhistory;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.link.Link;

/**
 * Link which changes the #hashtag of an URL
 * HTML code could be <a wicket:id="id">texto</a>
 * HTML code could be  &lt;a wicket:id="id"&gt;texto&lt;/a&gt;
 *
 * User: jcarrasco,obaisdefe
 * Date: 16-sep-2011
 */
public class AnchorAjaxLink extends Link {


    /**
     * Creates a link that changes the hastag of an URL
     * The id and the #hastag are the same
     *
     * @param id Wicket identificator and hashtag to be fired
     */
    public AnchorAjaxLink(String id) {
        this(id,id);
    }


    /**
     * Creates a link that changes the hastag of an URL
     * The id and the #hastag are the same
     *
     * @param id Wicket identificator
     * @param anchor Hashtag to be fired. Must not be null. If it begins with '#', the main value will be used; if not the '#' will be added
     */
    public AnchorAjaxLink(String id, String anchor) {
        super(id);
        updateAnchor(anchor);
        add(new AttributeModifier("onclick", ""));
    }

    /**
     * Updates hashtag value of this link
     * @param anchor New hashtag to be fired. Must not be null. If it begins with '#', the main value will be used; if not the '#' will be added
     */
    public void updateAnchor(String anchor){
        if (anchor == null) throw new IllegalArgumentException("Anchor must not be null"); //Not null
        // Just add attribute
        if (anchor.startsWith("#")) {
            add(new AttributeModifier("href", anchor));
        } else {
            add(new AttributeModifier("href", new StringBuilder().append('#').append(anchor).toString()));
        }
    }

    @Override
    public final void onClick() {
        //NOOOPE chuck tesla !!!
    }


}