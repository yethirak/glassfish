/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.acme;

import org.glassfish.tests.ejb.sample.SimpleEjb;
import embedded.util.ZipUtil;

import java.util.Map;
import java.util.HashMap;
import javax.ejb.*;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import com.sun.ejte.ccl.reporter.SimpleReporterAdapter;

public class Client {

    private static SimpleReporterAdapter stat =
        new SimpleReporterAdapter("appserv-tests");

    private static String appName;

    public static void main(String[] s) {
        appName = s[0];
        stat.addDescription(appName);
        Client t = new Client();
        try {
            t.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stat.printSummary(appName + "ID");
    }

    private void test() {

        Map<String, Object> p = new HashMap<String, Object>();
        p.put(EJBContainer.MODULES, "sample");
        p.put(EJBContainer.APP_NAME, "foo");

        EJBContainer c = null;
        try {
            c = EJBContainer.createEJBContainer(p);
            // ok now let's look up the EJB...
            Context ic = c.getContext();
            System.out.println("Looking up EJB...");
            SimpleEjb ejb = (SimpleEjb) ic.lookup("java:global/foo/sample/SimpleEjb");
            System.out.println("Invoking EJB...");
            System.out.println("EJB said: " + ejb.saySomething());
            System.out.println("JPA call returned: " + ejb.testJPA());

            stat.addStatus("EJB embedded with JPA", stat.PASS);
        } catch (Exception e) {
            stat.addStatus("EJB embedded with JPA", stat.FAIL);
            System.out.println("ERROR calling EJB:");
            e.printStackTrace();
            System.out.println("Saving the temp dir...:");
            ZipUtil.zipInstanceDirectory(appName);
        }
        System.out.println("Done calling EJB");

        if (c != null)
            c.close();

        System.out.println("..........FINISHED Embedded test");
    }
}
