/**
 * The MIT License
 * Copyright (c) 2018 Estonian Information System Authority (RIA), Nordic Institute for Interoperability Solutions (NIIS), Population Register Centre (VRK)
 * Copyright (c) 2015-2017 Estonian Information System Authority (RIA), Population Register Centre (VRK)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ee.ria.xroad.confproxy;

import ee.ria.xroad.common.SystemProperties;
import ee.ria.xroad.common.conf.globalconf.ConfigurationDirectory;
import ee.ria.xroad.confproxy.util.ConfProxyHelper;
import ee.ria.xroad.confproxy.util.OutputBuilder;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Defines a configuration proxy instance and carries out it's main operations.
 */
@Slf4j
public class ConfProxy {
    protected ConfProxyProperties conf;

    /**
     * Initializes a new configuration proxy instance.
     * @param instance name of this proxy instance
     * @throws Exception if loading instance configuration fails
     */
    ConfProxy(final String instance) throws Exception {
        this.conf = new ConfProxyProperties(instance);
        log.debug("Starting configuration-proxy '{}'...", instance);
    }

    /**
     * Launch the configuration proxy instance. Downloads signed directory,
     * signs it's content and moves it to the public distribution directory.
     * @throws Exception in case of any errors
     */
    public final void execute() throws Exception {
        log.debug("Purge outdated generations");
        ConfProxyHelper.purgeOutdatedGenerations(conf);
        for (int version = SystemProperties.CURRENT_GLOBAL_CONFIGURATION_VERSION;
             version >= SystemProperties.getMinimumConfigurationProxyGlobalConfigurationVersion();
             version--) {
            log.debug("Download global configuration version {}. Minimum version {}", version,
                    SystemProperties.getMinimumConfigurationProxyGlobalConfigurationVersion());
            ConfigurationDirectory confDir = download(version);
            log.debug("Create output builder");
            OutputBuilder output = new OutputBuilder(confDir, conf, version);
            log.debug("Build signed directory");
            output.buildSignedDirectory();
            log.debug("Move and cleanup");
            output.moveAndCleanup();
            log.debug("Finished execute");
        }
    }

    /**
     * Downloads the global configuration to configuration download path e.g. /etc/xroad/globalconf,
     * according to the instance configuration.
     * @return downloaded configuration directory
     * @throws Exception if configuration client script encounters errors
     */
    private ConfigurationDirectory download(int version) throws Exception {
        log.debug("Create directories");
        Files.createDirectories(Paths.get(conf.getConfigurationDownloadPath(version)));
        return ConfProxyHelper.downloadConfiguration(
                conf.getConfigurationDownloadPath(version),
                conf.getProxyAnchorPath(),
                version);
    }
}
