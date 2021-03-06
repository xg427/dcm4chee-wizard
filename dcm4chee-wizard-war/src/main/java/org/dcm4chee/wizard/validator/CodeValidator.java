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

package org.dcm4chee.wizard.validator;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * @author Robert David <robert.david@agfa.com>
 */
public class CodeValidator extends AbstractFormValidator {

    private static final long serialVersionUID = 1L;

    private final FormComponent<String>[] components;

    @SuppressWarnings("unchecked")
    public CodeValidator(FormComponent<String> codeValue, FormComponent<String> codingSchemeDesignator,
            FormComponent<String> codingSchemeVersion, FormComponent<String> codeMeaning) {
        components = new FormComponent[] { codeValue, codingSchemeDesignator, codingSchemeVersion, codeMeaning };
    }

    public FormComponent<?>[] getDependentFormComponents() {
        return components;
    }

    public void validate(Form<?> form) {

        final FormComponent<String> codeValue = components[0];
        final FormComponent<String> codingSchemeDesignator = components[1];
        final FormComponent<String> codingSchemeVersion = components[2];
        final FormComponent<String> codeMeaning = components[3];

        ValidationError validationError = new ValidationError();
        validationError.addKey("CodeValidator.invalid");

        if (!codeValue.getInput().isEmpty()) {
            if (codingSchemeDesignator.getInput().isEmpty())
                codingSchemeDesignator.error(validationError);
            if (codeMeaning.getInput().isEmpty())
                codeMeaning.error(validationError);
            return;
        }
        if (!codingSchemeDesignator.getInput().isEmpty()) {
            if (codeValue.getInput().isEmpty())
                codeValue.error(validationError);
            if (codeMeaning.getInput().isEmpty())
                codeMeaning.error(validationError);
            return;
        }
        if (!codeMeaning.getInput().isEmpty()) {
            if (codeValue.getInput().isEmpty())
                codeValue.error(validationError);
            if (codingSchemeDesignator.getInput().isEmpty())
                codingSchemeDesignator.error(validationError);
            return;
        }
        if (!codingSchemeVersion.getInput().isEmpty()) {
            if (codeValue.getInput().isEmpty())
                codeValue.error(validationError);
            if (codingSchemeDesignator.getInput().isEmpty())
                codingSchemeDesignator.error(validationError);
            if (codeMeaning.getInput().isEmpty())
                codeMeaning.error(validationError);
            return;
        }
    }
}
