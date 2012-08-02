##//////////////////////////////////////////////////////////////////////
##
##     Copyright (c) 2009-2012 Denim Group, Ltd.
##
##     The contents of this file are subject to the Mozilla Public License
##     Version 1.1 (the "License"); you may not use this file except in
##     compliance with the License. You may obtain a copy of the License at
##     http://www.mozilla.org/MPL/
##
##     Software distributed under the License is distributed on an "AS IS"
##     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
##     License for the specific language governing rights and limitations
##     under the License.
##
##     The Original Code is Vulnerability Manager.
##
##     The Initial Developer of the Original Code is Denim Group, Ltd.
##     Portions created by Denim Group, Ltd. are Copyright (C)
##     Denim Group, Ltd. All Rights Reserved.
##
##     Contributor(s): Denim Group, Ltd.
##
##//////////////////////////////////////////////////////////////////////

# lockdown.rb
# Ubuntu 12.04
# Denim Group 2012

# Do extra configuration to forward traffic through mod_jk / mod_security to a backend 
# tomcat server with security manager on.

execute "apt-get update" do
  command "apt-get update"
  action :run
end

execute "apt-get upgrade" do
	command "apt-get -y upgrade"
	action :run
end

server_package = package "libapache2-mod-jk"
server_package.run_action(:install)

server_package = package "libapache-mod-security"
server_package.run_action(:install)

server_package = package "libxml2-dev"
server_package.run_action(:install)

template "/etc/libapache2-mod-jk/workers.properties" do
  source "workers.properties.erb"
  owner "root"
  group "root"
  mode "0644"
  action :create
end

template "/etc/apache2/mods-enabled/jk.conf" do
  source "jk.conf.erb"
  owner "root"
  group "root"
  mode "0644"
end

script "get crs" do
  interpreter "bash"
  user "root"
  cwd "/vagrant"
  code <<-EOH
	cd /etc/apache2
    wget http://sourceforge.net/projects/mod-security/files/modsecurity-crs/0-CURRENT/modsecurity-crs_2.2.5.tar.gz
    tar xzf modsecurity-crs_2.2.5.tar.gz
    mv modsecurity-crs_2.2.5 modsecurity-crs
    cd modsecurity-crs
	sudo cp modsecurity_crs_10_setup.conf.example modsecurity_crs_10_config.conf
  EOH
end

template "/etc/apache2/httpd.conf" do
  source "httpd.conf.erb"
  owner "root"
  group "root"
  mode "0644"
end

directory "/etc/apache2/ssl" do
  owner "root"
  group "root"
  mode "0744"
  action :create
end

template "/etc/apache2/ssl/server.crt" do
  source "server.crt.erb"
  owner "root"
  group "root"
  mode "0644"
end

template "/etc/apache2/ssl/server.key" do
  source "server.key.erb"
  owner "root"
  group "root"
  mode "0644"
end

template "/etc/apache2/sites-enabled/000-default" do
  source "000-default.erb"
  owner "root"
  group "root"
  mode "0644"
end

template "/etc/apache2/sites-enabled/000-default-ssl" do
  source "000-default-ssl.erb"
  owner "root"
  group "root"
  mode "0644"
end

script "restart apache2" do
  interpreter "bash"
  user "root"
  cwd "/vagrant"
  code <<-EOH
    ln -s /etc/apache2/mods-available/rewrite.load /etc/apache2/mods-enabled/rewrite.load
    ln -s /etc/apache2/mods-available/ssl.load /etc/apache2/mods-enabled/mod_ssl.load
	ln -s /etc/apache2/mods-available/ssl.conf /etc/apache2/mods-enabled/mod_ssl.conf
	ln -s /etc/apache2/sites-available/default-ssl /etc/apache2/sites-enabled/000-default-ssl
	ln -s /usr/lib/i386-linux-gnu/libxml2.so.2 /usr/lib/libxml2.so.2
    service apache2 restart
  EOH
end

script "configure firewall" do
  interpreter "bash"
  user "root"
  code <<-EOH
	ufw default deny
	ufw allow 22
	ufw allow 443
	ufw allow 80
	ufw enable
  EOH
end

user "tfuser" do
  comment "ThreadFix users account"
  gid "admin"
  home "/home/tfuser"
  shell "/bin/bash"
  password "$1$qWfox.Mo$7S8/IUESEURVhu6fCB/mK1"
end