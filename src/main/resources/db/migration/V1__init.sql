CREATE TABLE `configuration`
(
    `guild_id`       varchar(21)                                         NOT NULL,
    `property_key`   varchar(45) CHARACTER SET latin1 COLLATE latin1_bin NOT NULL,
    `property_value` text CHARACTER SET latin1 COLLATE latin1_bin        NOT NULL,
    UNIQUE KEY `config_guild_key_unique` (`guild_id`, `property_key`),
    KEY `config_guild_id_idx` (`guild_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_bin;

CREATE TABLE `forbidden_words`
(
    `guild_id` varchar(21)             NOT NULL,
    `word`     text COLLATE latin1_bin NOT NULL,
    KEY `forb_words_guild_id_idx` (`guild_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_bin;

CREATE TABLE `tickets`
(
    `guild_id`               varchar(21)         NOT NULL,
    `user_id`                varchar(21)         NOT NULL,
    `creation_message_id`    varchar(21)         NOT NULL,
    `destination_message_id` varchar(21)         NOT NULL,
    `ticket_id`              int(10) unsigned    NOT NULL,
    `ticket_state`           tinyint(3) unsigned NOT NULL,
    UNIQUE KEY `tickets_ticket_id_unique` (`ticket_id`),
    KEY `tickets_guild_id_idx` (`guild_id`),
    KEY `tickets_loadable` (`guild_id`, `ticket_state`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_bin;