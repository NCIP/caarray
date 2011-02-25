DROP TABLE IF EXISTS `test`.`customer`;
CREATE TABLE  `test`.`customer` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `first` varchar(45) NOT NULL default '',
  `last` varchar(45) NOT NULL default '',
  `street` varchar(45) NOT NULL default '',
  `city` varchar(45) NOT NULL default '',
  `state` varchar(45) NOT NULL default '',
  `zip` varchar(45) NOT NULL default '',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `test`.`item`;
CREATE TABLE  `test`.`item` (
  `id` int(11) NOT NULL,
  `name` varchar(255) default NULL,
  `manufacturer` varchar(255) default NULL,
  `price` float default NULL,
  `customer_id` int(11) default NULL,
  PRIMARY KEY  (`id`),
  KEY `FK317B137BA932B2` (`customer_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;