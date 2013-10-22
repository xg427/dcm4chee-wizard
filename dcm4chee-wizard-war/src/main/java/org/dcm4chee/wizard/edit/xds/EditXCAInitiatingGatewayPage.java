/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is part of dcm4che, an implementation of DICOM(TM) in
 * Java(TM), hosted at https://github.com/dcm4che.
 *
 * The Initial Developer of the Original Code is
 * Agfa Healthcare.
 * Portions created by the Initial Developer are Copyright (C) 2012
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * See @authors listed below
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package org.dcm4chee.wizard.edit.xds;

import java.util.Arrays;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormSubmitBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.dcm4che.conf.api.ConfigurationException;
import org.dcm4che.net.Device;
import org.dcm4chee.wizard.common.component.ExtendedForm;
import org.dcm4chee.wizard.common.component.ModalWindowRuntimeException;
import org.dcm4chee.wizard.common.component.secure.SecureSessionCheckPage;
import org.dcm4chee.wizard.model.DeviceModel;
import org.dcm4chee.wizard.model.StringArrayModel;
import org.dcm4chee.wizard.model.xds.XCAInitiatingGatewayModel;
import org.dcm4chee.wizard.tree.ConfigTreeNode;
import org.dcm4chee.wizard.tree.ConfigTreeProvider;
import org.dcm4chee.xds2.conf.XCAInitiatingGWCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Backhaus <michael.backhaus@agfa.com>
 */
public class EditXCAInitiatingGatewayPage extends SecureSessionCheckPage{

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(EditXCAInitiatingGatewayPage.class);

    private List<Boolean> booleanChoice = Arrays.asList(new Boolean[] { true, false });

    // mandatory
    private Model<String> xdsApplicationNameModel;
    private Model<String> xdsHomeCommunityIdModel;
    private StringArrayModel xdsRespondingGatewayUrlModel;

    // optional
    private Model<String> xdsSoapMsgLogDirModel;
    private Model<Boolean> xdsAsyncModel;
    private Model<Boolean> xdsAsyncHandlerModel;
    private StringArrayModel xdsRespondingGatewayRetrieveUrlModel;
    private Model<String> xdsRegistryUrlModel;
    private StringArrayModel xdsRepositoryUrlModel;
    private Model<String> xdsPIXConsumerApplicationModel;
    private Model<String> xdsPIXManagerApplicationModel;
    private StringArrayModel xdsAssigningAuthorityModel;

    public EditXCAInitiatingGatewayPage(final ModalWindow window, XCAInitiatingGatewayModel model, 
            final ConfigTreeNode deviceNode) {
        super();
        try {
            add(new WebMarkupContainer("edit-xcainitiatinggateway-title").setVisible(model != null));
            setOutputMarkupId(true);
            final ExtendedForm form = new ExtendedForm("form");
            form.setResourceIdPrefix("dicom.edit.xds.");
            Device device = ((DeviceModel) deviceNode.getModel()).getDevice();
            initAttributes(device.getDeviceExtension(XCAInitiatingGWCfg.class));
            addMandatoryFormAttributes(form);
            addOptionalFormAttributes(form);
            addSaveButton(window, deviceNode, form);
            addCancelButton(window, form);
            add(form);
        } catch (ConfigurationException e) {
            log.error("{}: Error modifying XCA Initating Gateway: {}", this, e);
            if (log.isDebugEnabled())
                e.printStackTrace();
            throw new ModalWindowRuntimeException(e.getLocalizedMessage());
        }
    }

    private void addOptionalFormAttributes(ExtendedForm form) {
        final Form<?> optionalContainer = new Form<Object>("optional");
        optionalContainer.setOutputMarkupId(true);
        optionalContainer.setOutputMarkupPlaceholderTag(true);
        optionalContainer.setVisible(false);
        form.add(optionalContainer);
        addToggleOptionalCheckBox(form, optionalContainer);

        optionalContainer.add(new Label("xdsSoapMsgLogDir.label", new ResourceModel(
                "dicom.edit.xds.optional.xdsSoapMsgLogDir.label")).setOutputMarkupPlaceholderTag(true));
        optionalContainer.add(new TextField<String>("xdsSoapMsgLogDir", xdsSoapMsgLogDirModel).setType(String.class));

        optionalContainer.add(new Label("xdsAsync.label", new ResourceModel("dicom.edit.xds.optional.xdsAsync.label"))
                .setOutputMarkupPlaceholderTag(true));
        optionalContainer.add(new DropDownChoice<>("xdsAsync", xdsAsyncModel, booleanChoice).setNullValid(true));

        optionalContainer.add(new Label("xdsAsyncHandler.label", new ResourceModel(
                "dicom.edit.xds.optional.xdsAsyncHandler.label")).setOutputMarkupPlaceholderTag(true));
        optionalContainer.add(new DropDownChoice<>("xdsAsyncHandler", xdsAsyncHandlerModel, booleanChoice)
                .setNullValid(true));

        optionalContainer.add(new Label("xdsRespondingGatewayRetrieveUrl.label", new ResourceModel(
                "dicom.edit.xds.optional.xdsRespondingGatewayRetrieveUrl.label")).setOutputMarkupPlaceholderTag(true));
        optionalContainer.add(new TextArea<String>("xdsRespondingGatewayRetrieveUrl",
                xdsRespondingGatewayRetrieveUrlModel).setType(String.class));

        optionalContainer.add(new Label("xdsPIXConsumerApplication.label", new ResourceModel(
                "dicom.edit.xds.optional.xdsPIXConsumerApplication.label")).setOutputMarkupPlaceholderTag(true));
        optionalContainer.add(new TextField<String>("xdsPIXConsumerApplication", xdsPIXConsumerApplicationModel)
                .setType(String.class));

        optionalContainer.add(new Label("xdsPIXManagerApplication.label", new ResourceModel(
                "dicom.edit.xds.optional.xdsPIXManagerApplication.label")).setOutputMarkupPlaceholderTag(true));
        optionalContainer.add(new TextField<String>("xdsPIXManagerApplication", xdsPIXManagerApplicationModel)
                .setType(String.class));

        optionalContainer.add(new Label("xdsAssigningAuthority.label", new ResourceModel(
                "dicom.edit.xds.optional.xdsAssigningAuthority.label")).setOutputMarkupPlaceholderTag(true));
        optionalContainer.add(new TextArea<String>("xdsAssigningAuthority", xdsAssigningAuthorityModel)
                .setType(String.class));

        optionalContainer.add(new Label("xdsRegistryUrl.label", new ResourceModel(
                "dicom.edit.xds.optional.xdsRegistryUrl.label")).setOutputMarkupPlaceholderTag(true));
        optionalContainer.add(new TextField<String>("xdsRegistryUrl", xdsRegistryUrlModel).setType(String.class));

        optionalContainer.add(new Label("xdsRepositoryUrl.label", new ResourceModel(
                "dicom.edit.xds.optional.xdsRepositoryUrl.label")).setOutputMarkupPlaceholderTag(true));
        optionalContainer.add(new TextArea<String>("xdsRepositoryUrl", xdsRepositoryUrlModel).setType(String.class));
    }

    private void addToggleOptionalCheckBox(final ExtendedForm form, final Form<?> optionalContainer) {
        form.add(new Label("toggleOptional.label", new ResourceModel("dicom.edit.toggleOptional.label")));

        AjaxCheckBox ajaxCheckBox = new AjaxCheckBox("toggleOptional", new Model<Boolean>()) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(optionalContainer.setVisible(this.getModelObject()));
            }
        };

        AjaxFormSubmitBehavior onClick = new AjaxFormSubmitBehavior(optionalContainer, "change") {

            private static final long serialVersionUID = 1L;

            protected void onEvent(final AjaxRequestTarget target) {
                super.onEvent(target);
            }
        };

        ajaxCheckBox.add(onClick);
        form.add(ajaxCheckBox);
    }

    private void addMandatoryFormAttributes(ExtendedForm form) {
        Label applicationNameLabel = new Label("applicationName.label", new ResourceModel(
                "dicom.edit.xds.applicationName.label"));
        form.add(applicationNameLabel);
        FormComponent<String> applicationNameTextField = new TextField<String>("applicationName",
                xdsApplicationNameModel);
        applicationNameTextField.setType(String.class);
        applicationNameTextField.setRequired(true);
        form.add(applicationNameTextField);

        Label homeCommunityIdLabel = new Label("homeCommunityId.label", new ResourceModel(
                "dicom.edit.xds.homeCommunityId.label"));
        form.add(homeCommunityIdLabel);
        FormComponent<String> homeCommunityIdTextField = new TextField<String>("homeCommunityId",
                xdsHomeCommunityIdModel);
        homeCommunityIdTextField.setType(String.class);
        homeCommunityIdTextField.setRequired(true);
        form.add(homeCommunityIdTextField);

        Label respondingGatewayUrlLabel = new Label("respondingGatewayUrl.label", new ResourceModel(
                "dicom.edit.xds.respondingGatewayUrl.label"));
        form.add(respondingGatewayUrlLabel);
        FormComponent<String> respondingGatewayUrlTextArea = new TextArea<String>("respondingGatewayUrl",
                xdsRespondingGatewayUrlModel);
        respondingGatewayUrlTextArea.setType(String.class);
        respondingGatewayUrlTextArea.setRequired(true);
        form.add(respondingGatewayUrlTextArea);
    }

    private void initAttributes(XCAInitiatingGWCfg xcaInit) {
        if (xcaInit == null) {
            xdsApplicationNameModel = Model.of();
            xdsHomeCommunityIdModel = Model.of();
            xdsRespondingGatewayUrlModel = new StringArrayModel(null);
            xdsRespondingGatewayRetrieveUrlModel = new StringArrayModel(null);
            xdsRegistryUrlModel = Model.of();
            xdsRepositoryUrlModel = new StringArrayModel(null);
            xdsAsyncModel = Model.of();
            xdsAsyncHandlerModel = Model.of();
            xdsPIXConsumerApplicationModel = Model.of();
            xdsPIXManagerApplicationModel = Model.of();
            xdsAssigningAuthorityModel = new StringArrayModel(null);
            xdsSoapMsgLogDirModel = Model.of();
        } else {
            xdsApplicationNameModel = Model.of(xcaInit.getApplicationName());
            xdsHomeCommunityIdModel = Model.of(xcaInit.getHomeCommunityID());
            xdsRespondingGatewayUrlModel = new StringArrayModel(xcaInit.getRespondingGWURLs());
            xdsRespondingGatewayRetrieveUrlModel = new StringArrayModel(xcaInit.getRespondingGWRetrieveURLs());
            xdsRegistryUrlModel = Model.of(xcaInit.getRegistryURL());
            xdsRepositoryUrlModel = new StringArrayModel(xcaInit.getRepositoryURLs());
            xdsAsyncModel = Model.of(xcaInit.isAsync());
            xdsAsyncHandlerModel = Model.of(xcaInit.isAsyncHandler());
            xdsPIXConsumerApplicationModel = Model.of(xcaInit.getLocalPIXConsumerApplication());
            xdsPIXManagerApplicationModel = Model.of(xcaInit.getRemotePIXManagerApplication());
            xdsAssigningAuthorityModel = new StringArrayModel(xcaInit.getAssigningAuthorities());
            xdsSoapMsgLogDirModel = Model.of(xcaInit.getSoapLogDir());
        }
    }

    private void addCancelButton(final ModalWindow window, final ExtendedForm form) {
        form.add(new AjaxButton("cancel", new ResourceModel("cancelBtn"), form) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                window.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget arg0, Form<?> arg1) {
            }
        }.setDefaultFormProcessing(false));
    }

    private void addSaveButton(final ModalWindow window, final ConfigTreeNode deviceNode, final ExtendedForm form) {
        form.add(new IndicatingAjaxButton("submit", new ResourceModel("saveBtn"), form) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    Device device = ((DeviceModel) deviceNode.getModel()).getDevice();
                    XCAInitiatingGWCfg xca = device.getDeviceExtension(XCAInitiatingGWCfg.class);
                    // mandatory
                    xca.setApplicationName(xdsApplicationNameModel.getObject());
                    xca.setHomeCommunityID(xdsHomeCommunityIdModel.getObject());
                    xca.setRespondingGWURLs(xdsRespondingGatewayUrlModel.getArray());
                    // optional
                    if (xdsRespondingGatewayRetrieveUrlModel.getArray() != null)
                        xca.setRespondingGWRetrieveURLs(xdsRespondingGatewayRetrieveUrlModel.getArray());
                    if (xdsRegistryUrlModel.getObject() != null)
                        xca.setRegistryURL(xdsRegistryUrlModel.getObject());
                    if (xdsRepositoryUrlModel.getArray() != null)
                        xca.setRepositoryURLs(xdsRepositoryUrlModel.getArray());
                    if (xdsAsyncModel.getObject() != null)
                        xca.setAsync(xdsAsyncModel.getObject());
                    if (xdsAsyncHandlerModel.getObject() != null)
                        xca.setAsyncHandler(xdsAsyncHandlerModel.getObject());
                    if (xdsPIXConsumerApplicationModel.getObject() != null)
                        xca.setLocalPIXConsumerApplication(xdsPIXConsumerApplicationModel.getObject());
                    if (xdsPIXManagerApplicationModel.getObject() != null)
                        xca.setRemotePIXManagerApplication(xdsPIXManagerApplicationModel.getObject());
                    if (xdsAssigningAuthorityModel.getArray() != null)
                        xca.setAssigningAuthoritiesMap(xdsAssigningAuthorityModel.getArray());
                    if (xdsSoapMsgLogDirModel.getObject() != null)
                        xca.setSoapLogDir(xdsSoapMsgLogDirModel.getObject());
                    ConfigTreeProvider.get().mergeDevice(device);
                    window.close(target);
                } catch (Exception e) {
                    log.error("{}: Error modifying XCA Initating Gateway: {}", this, e);
                    if (log.isDebugEnabled())
                        e.printStackTrace();
                    throw new ModalWindowRuntimeException(e.getLocalizedMessage());
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                if (target != null)
                    target.add(form);
            }
        });
    }
}