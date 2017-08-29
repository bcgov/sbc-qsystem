SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

-- -----------------------------------------------------
-- Table `breaks`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `breaks` ;

CREATE  TABLE IF NOT EXISTS `breaks` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(245) NOT NULL DEFAULT 'Unknown' ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB, 
COMMENT = 'Списки наборов перерывов для привязки к дневному расписанию' ;


-- -----------------------------------------------------
-- Table `schedule`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `schedule` ;

CREATE  TABLE IF NOT EXISTS `schedule` (
  `id` BIGINT NOT NULL ,
  `name` VARCHAR(150) NOT NULL DEFAULT '' COMMENT 'Наименование плана' ,
  `type` INT NOT NULL DEFAULT 0 COMMENT 'Тип плана\n0 - недельный\n1 - четные/нечетные дни' ,
  `time_begin_1` TIME NULL DEFAULT NULL COMMENT 'Время начала работы' ,
  `time_end_1` TIME NULL DEFAULT NULL COMMENT 'Время завершения работы' ,
  `time_begin_2` TIME NULL DEFAULT NULL ,
  `time_end_2` TIME NULL DEFAULT NULL ,
  `time_begin_3` TIME NULL DEFAULT NULL ,
  `time_end_3` TIME NULL DEFAULT NULL ,
  `time_begin_4` TIME NULL DEFAULT NULL ,
  `time_end_4` TIME NULL DEFAULT NULL ,
  `time_begin_5` TIME NULL DEFAULT NULL ,
  `time_end_5` TIME NULL DEFAULT NULL ,
  `time_begin_6` TIME NULL DEFAULT NULL ,
  `time_end_6` TIME NULL DEFAULT NULL ,
  `time_begin_7` TIME NULL DEFAULT NULL ,
  `time_end_7` TIME NULL DEFAULT NULL ,
  `breaks_id1` BIGINT NULL ,
  `breaks_id2` BIGINT NULL ,
  `breaks_id3` BIGINT NULL ,
  `breaks_id4` BIGINT NULL ,
  `breaks_id5` BIGINT NULL ,
  `breaks_id6` BIGINT NULL ,
  `breaks_id7` BIGINT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_schedule_breaks1`
    FOREIGN KEY (`breaks_id1` )
    REFERENCES `breaks` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks2`
    FOREIGN KEY (`breaks_id2` )
    REFERENCES `breaks` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks3`
    FOREIGN KEY (`breaks_id7` )
    REFERENCES `breaks` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks4`
    FOREIGN KEY (`breaks_id3` )
    REFERENCES `breaks` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks5`
    FOREIGN KEY (`breaks_id4` )
    REFERENCES `breaks` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks6`
    FOREIGN KEY (`breaks_id5` )
    REFERENCES `breaks` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_schedule_breaks7`
    FOREIGN KEY (`breaks_id6` )
    REFERENCES `breaks` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
COMMENT = 'Справочник расписаний для услуг' ;

CREATE INDEX `idx_schedule_breaks1` ON `schedule` (`breaks_id1` ASC) ;

CREATE INDEX `idx_schedule_breaks2` ON `schedule` (`breaks_id2` ASC) ;

CREATE INDEX `idx_schedule_breaks3` ON `schedule` (`breaks_id7` ASC) ;

CREATE INDEX `idx_schedule_breaks4` ON `schedule` (`breaks_id3` ASC) ;

CREATE INDEX `idx_schedule_breaks5` ON `schedule` (`breaks_id4` ASC) ;

CREATE INDEX `idx_schedule_breaks6` ON `schedule` (`breaks_id5` ASC) ;

CREATE INDEX `idx_schedule_breaks7` ON `schedule` (`breaks_id6` ASC) ;


-- -----------------------------------------------------
-- Table `calendar`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `calendar` ;

CREATE  TABLE IF NOT EXISTS `calendar` (
  `id` BIGINT NOT NULL ,
  `name` VARCHAR(45) NOT NULL DEFAULT '' COMMENT 'Название календаря' ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB, 
COMMENT = 'Календарь услуг на год' ;


-- -----------------------------------------------------
-- Table `services`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `services` ;

CREATE  TABLE IF NOT EXISTS `services` (
  `id` BIGINT NOT NULL ,
  `name` VARCHAR(2000) NOT NULL COMMENT 'Наименование услуги' ,
  `description` VARCHAR(2000) NULL DEFAULT NULL COMMENT 'Описание услуги.' ,
  `service_prefix` VARCHAR(10) NULL DEFAULT '' ,
  `button_text` VARCHAR(2500) NOT NULL DEFAULT '' COMMENT 'HTML-текст для вывода на кнопки регистрации.' ,
  `status` INT NOT NULL DEFAULT 1 COMMENT 'Состояние услуги. 1 - доступна, 0 - недоступна, -1 - невидима.' ,
  `enable` INT NOT NULL DEFAULT 1 COMMENT 'Способ вызова клиента юзером\n1 - стандартно\n2 - backoffice, т.е. вызов следующего без табло и звука, запершение только редиректом' ,
  `prent_id` BIGINT NULL DEFAULT NULL COMMENT 'Групповое подчинение.' ,
  `day_limit` INT NOT NULL DEFAULT 0 COMMENT 'ограничение выданных билетов в день. 0-нет ограничения' ,
  `person_day_limit` INT NOT NULL DEFAULT 0 COMMENT 'ограничение выданных билетов в день клиентам с одинаковыми введенными данными. 0-нет ограничения' ,
  `advance_limit` INT NOT NULL DEFAULT 1 COMMENT 'Ограничение по количеству предварительно регистрировшихся в час' ,
  `advance_limit_period` INT NULL DEFAULT 14 COMMENT 'ограничение в днях, в пределах которого можно записаться вперед. может быть null или 0 если нет ограничения' ,
  `advance_time_period` INT NOT NULL DEFAULT 60 COMMENT 'периоды, на которые делится день, для записи предварительно' ,
  `schedule_id` BIGINT NULL DEFAULT NULL COMMENT 'План работы услуги' ,
  `input_required` TINYINT(1) NOT NULL DEFAULT false COMMENT 'Обязывать кастомера вводить что-то перед постоновкой в очередь' ,
  `input_caption` VARCHAR(2000) NOT NULL DEFAULT 'Введите номер документа' COMMENT 'Текст над полем ввода обязательного ввода' ,
  `result_required` TINYINT(1) NOT NULL DEFAULT false COMMENT 'Требовать ввод пользователем результата работы с клиентом' ,
  `calendar_id` BIGINT NULL DEFAULT NULL ,
  `pre_info_html` TEXT NOT NULL COMMENT 'html текст информационного сообщения перед постановкой в очередь' ,
  `pre_info_print_text` TEXT NOT NULL COMMENT 'текст для печати при необходимости перед постановкой в очередь' ,
  `point` INT NOT NULL DEFAULT 0 COMMENT 'указание для какого пункта регистрации услуга, 0-для всех, х-для киоска х.' ,
  `ticket_text` VARCHAR(1500) NULL COMMENT 'Текст напечатается на талоне.' ,
  `tablo_text` VARCHAR(1500) NOT NULL DEFAULT '' COMMENT 'Текст для вывода на главное табло в шаблоны панели вызванного и третью колонку пользователя',
  `seq_id` INT NOT NULL DEFAULT 0 COMMENT 'порядок следования кнопок услуг на пункте регистрации' ,
  `but_x` INT NOT NULL DEFAULT 0 COMMENT 'позиция кнопки' ,
  `but_y` INT NOT NULL DEFAULT 0 COMMENT 'позиция кнопки' ,
  `but_b` INT NOT NULL DEFAULT 0 COMMENT 'позиция кнопки' ,
  `but_h` INT NOT NULL DEFAULT 0 COMMENT 'позиция кнопки' ,
  `deleted` DATE NULL COMMENT 'признак удаления с проставленим даты' ,
  `duration` INT NOT NULL DEFAULT '1' COMMENT 'Норматив. Среднее время оказания этой услуги.  Пока для маршрутизации при медосмотре' ,
  `sound_template` VARCHAR(45) NULL COMMENT 'шаблон звукового приглашения. null или 0... - использовать родительский.' ,
  `expectation` INT NOT NULL DEFAULT 0 COMMENT 'Время обязательного ожидания посетителя' ,
  `link_service_id` BIGINT NULL COMMENT 'Услуга, в которую реально попадет клиент. А эта сама услуга чисто кнопка, ярлык.' ,
  `inputed_as_ext` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Разрешение выводить введенные данные в третью колонку на табло и в панель вызова',
  `inputed_as_number` INT NOT NULL DEFAULT 0 COMMENT 'Если требуется использовать введенные пользователем данные как его номер в очереди. 0 - генерировать как обычно',
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_servises_parent_id_servises_id`
    FOREIGN KEY (`prent_id` )
    REFERENCES `services` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_services_shedule`
    FOREIGN KEY (`schedule_id` )
    REFERENCES `schedule` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE,
  CONSTRAINT `fk_services_calendar`
    FOREIGN KEY (`calendar_id` )
    REFERENCES `calendar` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB, 
COMMENT = 'Дерево услуг' ;

CREATE INDEX `idx_servises_parent_id_servises_id` ON `services` (`prent_id` ASC) ;

CREATE INDEX `idx_services_shedule` ON `services` (`schedule_id` ASC) ;

CREATE INDEX `idx_services_calendar` ON `services` (`calendar_id` ASC) ;

-- -----------------------------------------------------
-- Table `spec_chedule`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `spec_schedule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `date_from` DATE NOT NULL COMMENT 'Это спец расписание для этого календаря действует с этой даты',
  `date_to` DATE NOT NULL COMMENT 'Это спец расписание для этого календаря действует до этой даты',
  `calendar_id` BIGINT NOT NULL COMMENT '',
  `schedule_id` BIGINT NOT NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  CONSTRAINT `fk_spec_schedule_calendar`
    FOREIGN KEY (`calendar_id`)
    REFERENCES `calendar` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_spec_schedule_schedule`
    FOREIGN KEY (`schedule_id`)
    REFERENCES `schedule` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
COMMENT = 'Специальные расписания для периодов для конкретных календарей. Перекрывают стардартное расписание.';

CREATE INDEX `idx_spec_chedule_calendar` ON `spec_schedule` (`calendar_id` ASC)  COMMENT '';

CREATE INDEX `idx_spec_chedule_schedule` ON `spec_schedule` (`schedule_id` ASC)  COMMENT '';



-- -----------------------------------------------------
-- Table `users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `users` ;

CREATE  TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор пользователя.' ,
  `name` VARCHAR(150) NOT NULL COMMENT 'Наименование' ,
  `password` VARCHAR(45) NOT NULL COMMENT 'Пароль пользователя.' ,
  `point` VARCHAR(45) NOT NULL COMMENT 'Идентификация рабочего места' ,
  `adress_rs` SMALLINT NOT NULL DEFAULT 0 COMMENT 'Адрес табло  пользователя в герлянде RS485' ,
  `enable` INT NOT NULL DEFAULT 1 COMMENT 'Дейсткующий пользователь или удаленный.' ,
  `admin_access` TINYINT(1) NOT NULL DEFAULT false COMMENT 'Доступ к администрирования системы.' ,
  `report_access` TINYINT(1) NOT NULL DEFAULT false COMMENT 'Доступ к получению отчетов.' ,
  `parallel_access` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Разрешение вести параллельный прием',
  `point_ext` VARCHAR(1045) NOT NULL DEFAULT '' COMMENT 'Вывод в третью колонку на главном табло. html + клиент ###  окно @@@' ,
  `tablo_text` VARCHAR(1500) NOT NULL DEFAULT '' COMMENT 'Текст для вывода на главное табло в шаблоны панели вызванного и третью колонку пользователя',
  `deleted` DATE NULL COMMENT 'признак удаления с проставлением даты' ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB, 
COMMENT = 'Пользователи системы.' ;


-- -----------------------------------------------------
-- Table `streets`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `streets` ;

CREATE  TABLE IF NOT EXISTS `streets` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(100) NOT NULL COMMENT 'Наименование улицы.' ,
  PRIMARY KEY (`id`, `name`) )
ENGINE = InnoDB, 
COMMENT = 'Словарь улиц' ;


-- -----------------------------------------------------
-- Table `clients_authorization`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `clients_authorization` ;

CREATE TABLE IF NOT EXISTS `clients_authorization` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `auth_id` VARCHAR(128) NULL COMMENT 'Если есть строковый идентификатор, то можно использовать',
  `name` VARCHAR(45) NOT NULL DEFAULT '' COMMENT 'Имя',
  `surname` VARCHAR(45) NOT NULL DEFAULT '' COMMENT 'Фамилие',
  `otchestvo` VARCHAR(45) NOT NULL DEFAULT '' COMMENT 'Отчество, иногда может отсутствовать.',
  `birthday` DATE NULL DEFAULT NULL COMMENT 'Дата рождения',
  `streets_id` BIGINT NULL DEFAULT NULL COMMENT 'Связь со словарем улиц. Проживание.',
  `house` VARCHAR(10) NULL DEFAULT '' COMMENT 'Номер дома',
  `korp` VARCHAR(10) NULL DEFAULT '' COMMENT 'Корпус дома',
  `flat` VARCHAR(10) NULL DEFAULT '' COMMENT 'Квартира',
  `validity` INT NOT NULL DEFAULT -1 COMMENT 'Степень валидности авторизованного клиента',
  `comments` VARCHAR(512) NULL COMMENT 'Некий необязательный комментарий',
  PRIMARY KEY (`id`)  COMMENT '',
  CONSTRAINT `fk_clients_authorization_streets`
    FOREIGN KEY (`streets_id`)
    REFERENCES `streets` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB
COMMENT = 'Словарь клиентов для авторизации.';

CREATE INDEX `idx_clients_authorization_streets` ON `clients_authorization` (`streets_id` ASC)  COMMENT '';

CREATE UNIQUE INDEX `idx_auth_id_UNIQUE` ON `clients_authorization` (`auth_id` ASC)  COMMENT '';


-- -----------------------------------------------------
-- Table `results`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `results` ;

CREATE  TABLE IF NOT EXISTS `results` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(150) NOT NULL DEFAULT '' COMMENT 'Текст результата' ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB, 
COMMENT = 'Справочник результатов работы с клиентом' ;


-- -----------------------------------------------------
-- Table `clients`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `clients` ;

CREATE  TABLE IF NOT EXISTS `clients` (
  `id` BIGINT NOT NULL COMMENT 'Первичный ключ.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.' ,
  `service_id` BIGINT NOT NULL COMMENT 'Услуга, к которой  пришел первоначально кастомер.  Вспомогательное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.' ,
  `user_id` BIGINT NOT NULL COMMENT ' Вспомогательное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.' ,
  `service_prefix` VARCHAR(45) NOT NULL COMMENT 'Префикс номера кастомера. Информационное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.' ,
  `number` INT NOT NULL COMMENT 'Номер клиента без префикса. Информационное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.' ,
  `stand_time` DATETIME NOT NULL COMMENT 'Время постановки в очередь. Информационное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.' ,
  `start_time` DATETIME NOT NULL COMMENT 'Время начала обработки клиента пользователем. Вспомогательное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.' ,
  `finish_time` DATETIME NOT NULL COMMENT 'Время завершения работы с клиентом пользователем. Информационное поле.\nВнимание! Вставлять и апдейтить записи только по завершению работы пользователя с кастомером.' ,
  `clients_authorization_id` BIGINT NULL DEFAULT NULL COMMENT 'Определено если клиент авторизовался' ,
  `result_id` BIGINT NULL DEFAULT NULL COMMENT 'Если выбрали результат работы' ,
  `input_data` VARCHAR(150) NOT NULL DEFAULT '' COMMENT 'Введенные данные пользователем' ,
  `state_in` INT NOT NULL DEFAULT 0 COMMENT 'клиент перешел в это состояние.' ,
  `welcome_time` DATETIME NOT NULL COMMENT 'timestamp as soon as client comes to CSR',
  `client_invite_time` DATETIME NOT NULL COMMENT 'timestamp when CSR invites customers',
  `service_start_time` DATETIME NOT NULL COMMENT 'timestamp when CSR starts service',
  `service_end_time` DATETIME NOT NULL COMMENT 'timestamp when CSR finishes service',
  `service_time_taken` int(11) NOT NULL COMMENT 'calculation for total service time',
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_сlients_service_id_services_id`
    FOREIGN KEY (`service_id` )
    REFERENCES `services` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_сlients_user_id_users_id`
    FOREIGN KEY (`user_id` )
    REFERENCES `users` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_clients_clients_authorization`
    FOREIGN KEY (`clients_authorization_id` )
    REFERENCES `clients_authorization` (`id` )
    ON DELETE SET NULL
    ON UPDATE SET NULL,
  CONSTRAINT `fk_clients_results`
    FOREIGN KEY (`result_id` )
    REFERENCES `results` (`id` )
    ON DELETE SET NULL
    ON UPDATE SET NULL)
ENGINE = InnoDB, 
COMMENT = 'Таблица регистрации статистических событий клиентов.' ;

CREATE INDEX `idx_сlients_service_id_services_id` ON `clients` (`service_id` ASC) ;

CREATE INDEX `idx_сlients_user_id_users_id` ON `clients` (`user_id` ASC) ;

CREATE INDEX `idx_clients_clients_authorization` ON `clients` (`clients_authorization_id` ASC) ;

CREATE INDEX `idx_clients_results` ON `clients` (`result_id` ASC) ;


-- -----------------------------------------------------
-- Table `net`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `net` ;

CREATE  TABLE IF NOT EXISTS `net` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT 'ПортСервера=\"3128\" ПортВебСервера=\"8080\" ПортКлиента=\"3129\" АдресСервера=\"localhost\"' ,
  `server_port` INT NOT NULL COMMENT 'Серверный порт приема заданий по сети от клиетских приложений.' ,
  `web_server_port` INT NOT NULL COMMENT 'Серверный порт для приема web запросов в системе отчетов.' ,
  `client_port` INT NOT NULL COMMENT 'UDP Порт клиента, на который идет рассылка широковещательных пакетов.' ,
  `finish_time` TIME NOT NULL COMMENT 'Время прекращения приема заявок на постановку в очередь' ,
  `start_time` TIME NOT NULL COMMENT 'Время начала приема заявок на постановку в очередь' ,
  `version` VARCHAR(25) NOT NULL DEFAULT 'Не присвоена' COMMENT 'Версия БД' ,
  `first_number` INT NOT NULL DEFAULT 1 ,
  `last_number` INT NOT NULL DEFAULT 999 ,
  `numering` TINYINT(1) NOT NULL DEFAULT true COMMENT '0 общая нумерация, 1 для каждой услуги своя нумерация' ,
  `point` INT NOT NULL DEFAULT 0 COMMENT '0 кабинет, 1 окно, 2 стойка' ,
  `sound` INT NOT NULL DEFAULT 2 COMMENT '0 нет оповещения, 1 только сигнал, 2 сигнал+голос' ,
  `branch_id` BIGINT NOT NULL DEFAULT -1 ,
  `sky_server_url` VARCHAR(145) NOT NULL DEFAULT '' ,
  `zone_board_serv_addr` VARCHAR(145) NOT NULL DEFAULT '' ,
  `zone_board_serv_port` BIGINT NOT NULL DEFAULT 0 ,
  `voice` INT NOT NULL DEFAULT 0 COMMENT '0 - по умолчанию, ну и т.д. по набору звуков' ,
  `black_time` INT NOT NULL DEFAULT 0 COMMENT 'Время нахождения в блеклисте в минутах. 0 - попавшие в блекслист не блокируются' ,
  `limit_recall` INT NOT NULL DEFAULT 0 COMMENT 'Количество повторных вызовов перед отклонением неявившегося посетителя, 0-бесконечно' ,
  `button_free_design` TINYINT(1)  NOT NULL DEFAULT false COMMENT 'авторасстановка кнопок на киоске' ,
  `ext_priority` INT NOT NULL DEFAULT 0 COMMENT 'Количество дополнительных приоритетов',
  PRIMARY KEY (`id`) )
ENGINE = InnoDB, 
COMMENT = 'Сетевые настройки сервера.' ;


-- -----------------------------------------------------
-- Table `services_users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `services_users` ;

CREATE  TABLE IF NOT EXISTS `services_users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `service_id` BIGINT NOT NULL ,
  `user_id` BIGINT NOT NULL ,
  `coefficient` INT NOT NULL DEFAULT 1 COMMENT 'Коэффициент участия. 0/1/2' ,
  `flexible_coef` TINYINT(1)  NOT NULL DEFAULT false COMMENT 'возможность изменить коэф участия юзерами' ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_services_id_su_service_id`
    FOREIGN KEY (`service_id` )
    REFERENCES `services` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_userss_id_su_user_id`
    FOREIGN KEY (`user_id` )
    REFERENCES `users` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB, 
COMMENT = 'Таблица соответствий услуга - пользователь.' ;

CREATE INDEX `idx_services_id_su_service_id` ON `services_users` (`service_id` ASC) ;

CREATE INDEX `idx_userss_id_su_user_id` ON `services_users` (`user_id` ASC) ;


-- -----------------------------------------------------
-- Table `statistic`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `statistic` ;

CREATE  TABLE IF NOT EXISTS `statistic` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `user_id` BIGINT NOT NULL ,
  `client_id` BIGINT NOT NULL ,
  `service_id` BIGINT NOT NULL ,
  `results_id` BIGINT NULL ,
  `user_start_time` DATETIME NOT NULL COMMENT 'Время начала обработки кастомера юзером.' ,
  `user_finish_time` DATETIME NOT NULL COMMENT 'Время завершения обработки кастомера юзером.' ,
  `client_stand_time` DATETIME NOT NULL COMMENT 'Время постановки кастомера в очередь' ,
  `user_work_period` INT NOT NULL COMMENT 'Время работы пользователя с клиентом в минутах.' ,
  `client_wait_period` INT NOT NULL COMMENT 'Время ожидания в минутах. Определяется триггером.' ,
  `state_in` INT NOT NULL DEFAULT 0 COMMENT 'Клиент перешел в это состояние' ,
  `client_welcome_time` DATETIME NOT NULL COMMENT 'timestamp as soon as client comes to CSR',
  `client_invite_time` DATETIME NOT NULL COMMENT 'timestamp when CSR invites customers',
  `service_start_time` DATETIME NOT NULL COMMENT 'timestamp when CSR starts service',
  `service_end_time` DATETIME NOT NULL COMMENT 'timestamp when CSR finishes service',
  `service_time_taken` int(11) NOT NULL COMMENT 'calculation for total service time',
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_work_user_id_users_id`
    FOREIGN KEY (`user_id` )
    REFERENCES `users` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_work_сlient_id_сlients_id`
    FOREIGN KEY (`client_id` )
    REFERENCES `clients` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_work_service_id_services_id`
    FOREIGN KEY (`service_id` )
    REFERENCES `services` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_statistic_results`
    FOREIGN KEY (`results_id` )
    REFERENCES `results` (`id` )
    ON DELETE SET NULL
    ON UPDATE SET NULL)
ENGINE = InnoDB, 
COMMENT = 'События работы пользователя с клиентом.Формируется триггером' ;

CREATE INDEX `idx_work_user_id_users_id` ON `statistic` (`user_id` ASC) ;

CREATE INDEX `idx_work_сlient_id_сlients_id` ON `statistic` (`client_id` ASC) ;

CREATE INDEX `idx_work_service_id_services_id` ON `statistic` (`service_id` ASC) ;

CREATE INDEX `idx_statistic_results` ON `statistic` (`results_id` ASC) ;


-- -----------------------------------------------------
-- Table `reports`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reports` ;

CREATE  TABLE IF NOT EXISTS `reports` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(255) NOT NULL COMMENT 'Название отчета, выводимое пользователю.' ,
  `className` VARCHAR(150) NOT NULL COMMENT 'Класс формирования отчета. Полное наименование класса с пакетами.' ,
  `template` VARCHAR(150) NOT NULL COMMENT 'Шаблон отчета. Хранится в отдельном пакете в jar.' ,
  `href` VARCHAR(150) NOT NULL COMMENT 'Ссылка на отчет в index.html. Без расширения типа файла.' ,
  PRIMARY KEY (`id`, `href`) )
ENGINE = InnoDB, 
COMMENT = 'Зарегистрированные аналитические отчеты.' ;


-- -----------------------------------------------------
-- Table `advance`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `advance` ;

CREATE  TABLE IF NOT EXISTS `advance` (
  `id` BIGINT NOT NULL ,
  `service_id` BIGINT NOT NULL COMMENT 'Услуга предварительной записи' ,
  `advance_time` DATETIME NOT NULL COMMENT 'Время предварительной записи' ,
  `priority` INT NOT NULL DEFAULT 2 COMMENT 'Приоритет заранее записавшегося клиента.' ,
  `clients_authorization_id` BIGINT NULL DEFAULT NULL COMMENT 'Определено если клиент авторизовался' ,
  `input_data` VARCHAR(150) NULL COMMENT 'Введеные при предвариловке данные клиента если услуга этого требует' ,
  `comments` VARCHAR(345) NULL DEFAULT '' COMMENT 'Коментарии при записи предварительно оператором удаленно' ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_scenario_services`
    FOREIGN KEY (`service_id` )
    REFERENCES `services` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_advance_clients_authorization`
    FOREIGN KEY (`clients_authorization_id` )
    REFERENCES `clients_authorization` (`id` )
    ON DELETE SET NULL
    ON UPDATE CASCADE)
ENGINE = InnoDB, 
COMMENT = 'Таблица предварительной записи' ;

CREATE INDEX `idx_scenario_services` ON `advance` (`service_id` ASC) ;

CREATE INDEX `idx_advance_clients_authorization` ON `advance` (`clients_authorization_id` ASC) ;


-- -----------------------------------------------------
-- Table `information`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `information` ;

CREATE  TABLE IF NOT EXISTS `information` (
  `id` BIGINT NOT NULL ,
  `parent_id` BIGINT NULL DEFAULT NULL ,
  `name` VARCHAR(100) NOT NULL COMMENT 'Наименование узла справки' ,
  `text` TEXT NOT NULL COMMENT 'html-текст справки' ,
  `text_print` TEXT NOT NULL COMMENT 'Текст для печати информационного узла' ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_information_information`
    FOREIGN KEY (`parent_id` )
    REFERENCES `information` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB, 
COMMENT = 'Таблица справочной информации древовидной структуры' ;

CREATE INDEX `idx_information_information` ON `information` (`parent_id` ASC) ;


-- -----------------------------------------------------
-- Table `responses`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `responses` ;

CREATE TABLE IF NOT EXISTS `responses` (
  `id` BIGINT NOT NULL COMMENT '',
  `parent_id` BIGINT NULL COMMENT '',
  `name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '',
  `text` VARCHAR(5000) NOT NULL DEFAULT '' COMMENT '',
  `input_caption` VARCHAR(512) NOT NULL DEFAULT '' COMMENT '',
  `input_required` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '',
  `deleted` DATE NULL COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  CONSTRAINT `fk_responses_responses`
    FOREIGN KEY (`parent_id`)
    REFERENCES `responses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
COMMENT = 'Список отзывов в отратной связи';

CREATE INDEX `idx_responses_responses` ON `responses` (`parent_id` ASC)  COMMENT '';


-- -----------------------------------------------------
-- Table `response_event`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `response_event` ;

CREATE TABLE IF NOT EXISTS `response_event` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '',
  `resp_date` DATETIME NOT NULL COMMENT 'Дата отклика',
  `response_id` BIGINT NOT NULL COMMENT '',
  `services_id` BIGINT NULL COMMENT '',
  `users_id` BIGINT NULL COMMENT '',
  `clients_id` BIGINT NULL COMMENT 'Клиент оставивший отзыв',
  `client_data` VARCHAR(245) NOT NULL DEFAULT '' COMMENT '',
  `comment` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '',
  PRIMARY KEY (`id`)  COMMENT '',
  CONSTRAINT `fk_response_date_responses`
    FOREIGN KEY (`response_id`)
    REFERENCES `responses` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_response_event_services`
    FOREIGN KEY (`services_id`)
    REFERENCES `services` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_response_event_users`
    FOREIGN KEY (`users_id`)
    REFERENCES `users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_response_event_clients`
    FOREIGN KEY (`clients_id`)
    REFERENCES `clients` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
COMMENT = 'Даты оставленных отзывов.';

CREATE INDEX `idx_response_date_responses` ON `response_event` (`response_id` ASC)  COMMENT '';

CREATE INDEX `idx_response_event_services` ON `response_event` (`services_id` ASC)  COMMENT '';

CREATE INDEX `idx_response_event_users` ON `response_event` (`users_id` ASC)  COMMENT '';

CREATE INDEX `idx_response_event_clients` ON `response_event` (`clients_id` ASC)  COMMENT '';


-- -----------------------------------------------------
-- Table `calendar_out_days`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `calendar_out_days` ;

CREATE  TABLE IF NOT EXISTS `calendar_out_days` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `out_day` DATE NOT NULL COMMENT 'Дата неработы. Важен месяц и день' ,
  `calendar_id` BIGINT NOT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_calendar_out_days_calendar`
    FOREIGN KEY (`calendar_id` )
    REFERENCES `calendar` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB, 
COMMENT = 'Дни неработы услуг' ;

CREATE INDEX `idx_calendar_out_days_calendar` ON `calendar_out_days` (`calendar_id` ASC) ;


-- -----------------------------------------------------
-- Table `break`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `break` ;

CREATE  TABLE IF NOT EXISTS `break` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `breaks_id` BIGINT NULL ,
  `from_time` TIME NOT NULL ,
  `to_time` TIME NOT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_break_breaks1`
    FOREIGN KEY (`breaks_id` )
    REFERENCES `breaks` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB, 
COMMENT = 'Перерывы в работе для предвариловки' ;

CREATE INDEX `idx_break_breaks1` ON `break` (`breaks_id` ASC) ;


-- -----------------------------------------------------
-- Table `services_langs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `services_langs` ;

CREATE  TABLE IF NOT EXISTS `services_langs` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `services_id` BIGINT NULL ,
  `lang` VARCHAR(45) NOT NULL ,
  `name` VARCHAR(2000) NOT NULL DEFAULT '' ,
  `description` VARCHAR(2000) NULL ,
  `button_text` VARCHAR(2500) NOT NULL DEFAULT '' ,
  `input_caption` VARCHAR(2000) NOT NULL DEFAULT '' ,
  `ticket_text` VARCHAR(1500) NULL ,
  `tablo_text` VARCHAR(1500) NOT NULL DEFAULT '' ,
  `pre_info_html` TEXT NOT NULL ,
  `pre_info_print_text` TEXT NOT NULL ,
  PRIMARY KEY (`id`) ,
  CONSTRAINT `fk_services_langs_services`
    FOREIGN KEY (`services_id` )
    REFERENCES `services` (`id` )
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB;

CREATE INDEX `idx_services_langs_services` ON `services_langs` (`services_id` ASC) ;


-- -----------------------------------------------------
-- Table `standards`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `standards` ;

CREATE  TABLE IF NOT EXISTS `standards` (
  `id` BIGINT NOT NULL AUTO_INCREMENT ,
  `wait_max` INT NOT NULL DEFAULT 0 COMMENT 'Максимальное время ожидания, в минутах' ,
  `work_max` INT NOT NULL DEFAULT 0 COMMENT 'Максимальное время работы с одним клиентом, в минутах' ,
  `downtime_max` INT NOT NULL DEFAULT 0 COMMENT 'Максимальное время простоя при наличии очереди, в минутах' ,
  `line_service_max` INT NOT NULL DEFAULT 0 COMMENT 'Максимальная длинна очереди к одной услуге' ,
  `line_total_max` INT NOT NULL DEFAULT 0 COMMENT 'Максимальное количество клиентов ко всем услугам' ,
  `relocation` INT NOT NULL DEFAULT 1 COMMENT 'типа параметр если есть перемещение, например между корпусами или ходьба до оператора' ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `properties`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `properties` ;

CREATE TABLE IF NOT EXISTS `properties` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `hide` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Показывать пользователю этот параметр или нет',
  `psection` VARCHAR(128) NULL COMMENT 'Раздел',
  `pkey` VARCHAR(128) NOT NULL COMMENT 'Ключ',
  `pvalue` VARCHAR(10240) NULL COMMENT 'Значение',
  `pcomment` VARCHAR(256) NULL,
  `pdata` TEXT NULL COMMENT 'Не типизированные текстовые данные.',
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `section_key_idx` ON `properties` (`psection` ASC, `pkey` ASC);




DELIMITER $$


DROP TRIGGER IF EXISTS `insert_to_statistic` $$



CREATE TRIGGER insert_to_statistic 
    AFTER INSERT ON clients
    FOR EACH ROW
BEGIN
    SET @finish_start= TIMEDIFF(NEW.finish_time, NEW.start_time);
    SET @start_starnd = TIMEDIFF(NEW.start_time, NEW.stand_time);
    INSERT
        INTO statistic(state_in, results_id, user_id, client_id, service_id, user_start_time, user_finish_time, client_stand_time, user_work_period, client_wait_period, client_welcome_time, client_invite_time, service_start_time, service_end_time, service_time_taken) 
    VALUES
        (NEW.state_in, NEW.result_id, NEW.user_id, NEW.id, NEW.service_id, NEW.start_time, NEW.finish_time, NEW.stand_time, 
        round(
                (HOUR(@finish_start) * 60 * 60 +
                 MINUTE(@finish_start) * 60 +
                 SECOND(@finish_start) + 59)/60),
        round(
                (HOUR(@start_starnd) * 60 * 60 +
                MINUTE(@start_starnd) * 60 +
                SECOND(@start_starnd) + 59)/60)  
        , NEW.welcome_time, New.invite_time, New.service_start_time, New.service_end_time, New.service_time_taken);
END;$$



DROP TRIGGER IF EXISTS `update_to_statistic` $$



CREATE TRIGGER update_to_statistic
    AFTER UPDATE ON clients
    FOR EACH ROW
BEGIN
    SET @finish_start= TIMEDIFF(NEW.finish_time, NEW.start_time);
    SET @start_starnd = TIMEDIFF(NEW.start_time, NEW.stand_time);
    INSERT
        INTO statistic(state_in, results_id, user_id, client_id, service_id, user_start_time, user_finish_time, client_stand_time, user_work_period, client_wait_period, client_welcome_time, client_invite_time, service_start_time, service_end_time, service_time_taken) 
    VALUES
        (NEW.state_in, NEW.result_id, NEW.user_id, NEW.id, NEW.service_id, NEW.start_time, NEW.finish_time, NEW.stand_time, 
        round(
                (HOUR(@finish_start) * 60 * 60 +
                 MINUTE(@finish_start) * 60 +
                 SECOND(@finish_start) + 59)/60),
        round(
                (HOUR(@start_starnd) * 60 * 60 +
                MINUTE(@start_starnd) * 60 +
                SECOND(@start_starnd) + 59)/60)  
        , NEW.welcome_time, New.invite_time, New.service_start_time, New.service_end_time, New.service_time_taken);
END;$$


DELIMITER ;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `schedule`
-- -----------------------------------------------------

INSERT INTO `schedule` (`id`, `name`, `type`, `time_begin_1`, `time_end_1`, `time_begin_2`, `time_end_2`, `time_begin_3`, `time_end_3`, `time_begin_4`, `time_end_4`, `time_begin_5`, `time_end_5`, `time_begin_6`, `time_end_6`, `time_begin_7`, `time_end_7`, `breaks_id1`, `breaks_id2`, `breaks_id3`, `breaks_id4`, `breaks_id5`, `breaks_id6`, `breaks_id7`) VALUES (1, 'Working plan from 8.00 to 17.00', 0, '08:00:00', '17:00:00', '08:00:00', '17:00:00', '08:00:00', '17:00:00', '08:00:00', '17:00:00', '08:00:00', '17:00:00', '08:00:00', '17:00:00', '08:00:00', '17:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `schedule` (`id`, `name`, `type`, `time_begin_1`, `time_end_1`, `time_begin_2`, `time_end_2`, `time_begin_3`, `time_end_3`, `time_begin_4`, `time_end_4`, `time_begin_5`, `time_end_5`, `time_begin_6`, `time_end_6`, `time_begin_7`, `time_end_7`, `breaks_id1`, `breaks_id2`, `breaks_id3`, `breaks_id4`, `breaks_id5`, `breaks_id6`, `breaks_id7`) VALUES (2, 'Workplan for the odd / even', 1, '08:00:00	', '13:00:00', '12:00:00', '17:00:00', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);


-- -----------------------------------------------------
-- Data for table `calendar`
-- -----------------------------------------------------

INSERT INTO `calendar` (`id`, `name`) VALUES (1, 'Common calendar');


-- -----------------------------------------------------
-- Data for table `services`
-- -----------------------------------------------------

INSERT INTO `services` (`id`, `name`, `description`, `service_prefix`, `button_text`, `status`, `enable`, `prent_id`, `day_limit`, `person_day_limit`, `advance_limit`, `advance_limit_period`, `advance_time_period`, `schedule_id`, `input_required`, `input_caption`, `result_required`, `calendar_id`, `pre_info_html`, `pre_info_print_text`, `point`, `ticket_text`, `tablo_text`, `seq_id`, `but_x`, `but_y`, `but_b`, `but_h`, `deleted`, `duration`, `sound_template`, `expectation`, `link_service_id`, `inputed_as_ext`) VALUES (1, 'Root of services', 'Root of services', '-', "<html><p align=center><span style=\'font-size:55.0;color:#DC143C\'>QMS QSystem</span><br><span style=\'font-size:45.0;color:#DC143C\'><i>select a service</i>", 1, 1, NULL, 0, 0, 1, 14, 60, NULL, 0, '', 0, NULL, '', '', 0, NULL, '', 0, 100, 100, 200, 100, NULL, 1, '120050', 0, NULL, false);
INSERT INTO `services` (`id`, `name`, `description`, `service_prefix`, `button_text`, `status`, `enable`, `prent_id`, `day_limit`, `person_day_limit`, `advance_limit`, `advance_limit_period`, `advance_time_period`, `schedule_id`, `input_required`, `input_caption`, `result_required`, `calendar_id`, `pre_info_html`, `pre_info_print_text`, `point`, `ticket_text`, `tablo_text`, `seq_id`, `but_x`, `but_y`, `but_b`, `but_h`, `deleted`, `duration`, `sound_template`, `expectation`, `link_service_id`, `inputed_as_ext`) VALUES (2, 'Service', 'Description of service', 'A', "<html><b><p align=center><span style=\'font-size:20.0pt;color:blue\'>Some service", 1, 1, 1, 0, 0, 1, 14, 60, 1, 0, '', 0, 1, '', '', 0, NULL, '', 0, 100, 100, 200, 100, NULL, 1, '021111', 0, NULL, false);



-- -----------------------------------------------------
-- Data for table `users`
-- -----------------------------------------------------

INSERT INTO `users` (`id`, `name`, `password`, `point`, `adress_rs`, `enable`, `admin_access`, `report_access`, `parallel_access`, `point_ext`, `tablo_text`, `deleted`) VALUES (1, 'Administrator', '', '1', 32, 1, 1, 1, DEFAULT, '', '', NULL);
INSERT INTO `users` (`id`, `name`, `password`, `point`, `adress_rs`, `enable`, `admin_access`, `report_access`, `parallel_access`, `point_ext`, `tablo_text`, `deleted`) VALUES (2, 'Operator', '', '2', 33, 1, 0, 0, DEFAULT, '<html><span style=\'font-size:26.0pt;color:blue\'>Floor 1<br>Office 1 #user #service #inputed', '', NULL);


-- -----------------------------------------------------
-- Data for table `results`
-- -----------------------------------------------------

INSERT INTO `results` (`id`, `name`) VALUES (1, 'Complite');
INSERT INTO `results` (`id`, `name`) VALUES (2, 'Problems...');
INSERT INTO `results` (`id`, `name`) VALUES (3, 'Solving...');

-- -----------------------------------------------------
-- Data for table `net`
-- -----------------------------------------------------

INSERT INTO `net` (`id`, `server_port`, `web_server_port`, `client_port`, `finish_time`, `start_time`, `version`, `first_number`, `last_number`, `numering`, `point`, `sound`, `branch_id`, `sky_server_url`, `zone_board_serv_addr`, `zone_board_serv_port`, `voice`, `black_time`, `limit_recall`, `button_free_design`, `ext_priority`) VALUES (1, 3128, 8088, 3129, '18:00:00', '08:45:00', '4', 1, 999, 0, 0, 1, 113, 'http://localhost:8080/qskyapi/customer_events?wsdl', '127.0.0.1', 27007, 0, 0, 0, 0, 0);


-- -----------------------------------------------------
-- Data for table `services_users`
-- -----------------------------------------------------

INSERT INTO `services_users` (`id`, `service_id`, `user_id`, `coefficient`, `flexible_coef`) VALUES (1, 2, 2, 1, 0);


-- -----------------------------------------------------
-- Data for table `reports`
-- -----------------------------------------------------

INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (1, 'Статистический отчет в разрезе услуг за период', 'ru.apertum.qsystem.reports.formirovators.StatisticServices', '/ru/apertum/qsystem/reports/templates/statisticServicesPeriod.jasper', 'statistic_period_services');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (2, 'Статистический отчет в разрезе персонала за период', 'ru.apertum.qsystem.reports.formirovators.StatisticUsers', '/ru/apertum/qsystem/reports/templates/statisticUsersPeriod.jasper', 'statistic_period_users');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (3, 'Отчет по распределению клиентов по виду услуг за период', 'ru.apertum.qsystem.reports.formirovators.RatioServices', '/ru/apertum/qsystem/reports/templates/ratioServicesPeriod.jasper', 'ratio_period_services');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (4, 'Распределение нагрузки внутри дня', 'ru.apertum.qsystem.reports.formirovators.DistributionJobDay', '/ru/apertum/qsystem/reports/templates/DistributionJobDay.jasper', 'distribution_job_day');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (5, 'Распределение нагрузки внутри дня для услуги', 'ru.apertum.qsystem.reports.formirovators.DistributionJobDayServices', '/ru/apertum/qsystem/reports/templates/DistributionJobDayServices.jasper', 'distribution_job_services');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (6, 'Распределение нагрузки внутри дня для пользователя', 'ru.apertum.qsystem.reports.formirovators.DistributionJobDayUsers', '/ru/apertum/qsystem/reports/templates/DistributionJobDayUsers.jasper', 'distribution_job_users');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (7, 'Распределение среднего времени ожидания внутри дня', 'ru.apertum.qsystem.reports.formirovators.DistributionWaitDay', '/ru/apertum/qsystem/reports/templates/DistributionWaitDay.jasper', 'distribution_wait_day');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (8, 'Распределение среднего времени ожидания внутри дня для услуги', 'ru.apertum.qsystem.reports.formirovators.DistributionWaitDayServices', '/ru/apertum/qsystem/reports/templates/DistributionWaitDayServices.jasper', 'distribution_wait_services');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (9, 'Распределение среднего времени ожидания внутри дня для пользователя', 'ru.apertum.qsystem.reports.formirovators.DistributionWaitDayUsers', '/ru/apertum/qsystem/reports/templates/DistributionWaitDayUsers.jasper', 'distribution_wait_users');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (10, 'Статистический отчет по отзывам клиентов за период', 'ru.apertum.qsystem.reports.formirovators.ResponsesReport', '/ru/apertum/qsystem/reports/templates/responsesReport.jasper', 'statistic_period_responses');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (11, 'Полный отчет по отзывам клиентов за период', 'ru.apertum.qsystem.reports.formirovators.ResponsesDateReport', '/ru/apertum/qsystem/reports/templates/responsesDateReport.jasper', 'statistic_period_date_responses');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (12, 'Отчет предварительно зарегистрированных клиентов по услуге на дату', 'ru.apertum.qsystem.reports.formirovators.DistributionMedDayServices', '/ru/apertum/qsystem/reports/templates/DistributionMedDayServices.jasper', 'distribution_med_services');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (13, 'Отчет по авторизованным персонам за период для пользователя', 'ru.apertum.qsystem.reports.formirovators.AuthorizedClientsPeriodUsers', '/ru/apertum/qsystem/reports/templates/AuthorizedClientsPeriodUsers.jasper', 'authorized_clients_period_users');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (14, 'Отчет по авторизованным персонам за период для услуги', 'ru.apertum.qsystem.reports.formirovators.AuthorizedClientsPeriodServices', '/ru/apertum/qsystem/reports/templates/AuthorizedClientsPeriodServices.jasper', 'authorized_clients_period_services');
INSERT INTO `reports` (`id`, `name`, `className`, `template`, `href`) VALUES (15, 'Отчет по результатам работы за период в разрезе услуг', 'ru.apertum.qsystem.reports.formirovators.ResultStateServices', '/ru/apertum/qsystem/reports/templates/resultStateServicesPeriod.jasper', 'result_state_services');


-- -----------------------------------------------------
-- Data for table `information`
-- -----------------------------------------------------

INSERT INTO `information` (`id`, `parent_id`, `name`, `text`, `text_print`) VALUES (1, NULL, 'Infosystem', '<html><p align=center><span style=\'font-size:55.0;color:#DC143C\'>Infosystem<br><span style=\'font-size:45.0;color:#DC143C\'><i>Read and print the note</i></span></p>', 'Invite a manager for information');


-- -----------------------------------------------------
-- Data for table `responses`
-- -----------------------------------------------------

INSERT INTO `responses` (`id`, `parent_id`, `name`, `text`, `input_caption`, `input_required`, `deleted`) VALUES (0, NULL, 'Responses', '<html><p  style=\'text-align: center;\'><font size=\'10\' color=\'#ffff4d\'>Help us to improve our work.</font><br><font  size=\'4\' color=\'#ffff4d\'>Each of your feedback is very important to us.</font></p>', DEFAULT, DEFAULT, NULL);
INSERT INTO `responses` (`id`, `parent_id`, `name`, `text`, `input_caption`, `input_required`, `deleted`) VALUES (1, 0, 'Excellent', '<html><b><p align=center><span style=\'font-size:20.0pt;color:green\'>Excellent</span></b>', DEFAULT, DEFAULT, NULL);
INSERT INTO `responses` (`id`, `parent_id`, `name`, `text`, `input_caption`, `input_required`, `deleted`) VALUES (2, 0, 'Good', '<html><b><p align=center><span style=\'font-size:20.0pt;color:green\'>Good</span></b>', DEFAULT, DEFAULT, NULL);
INSERT INTO `responses` (`id`, `parent_id`, `name`, `text`, `input_caption`, `input_required`, `deleted`) VALUES (3, 0, 'So, so...', '<html><b><p align=center><span style=\'font-size:20.0pt;color:green\'>So, so...</span></b>', DEFAULT, DEFAULT, NULL);
INSERT INTO `responses` (`id`, `parent_id`, `name`, `text`, `input_caption`, `input_required`, `deleted`) VALUES (4, 0, 'Bad', '<html><b><p align=center><span style=\'font-size:20.0pt;color:green\'>Bad</span></b>', DEFAULT, DEFAULT, NULL);
INSERT INTO `responses` (`id`, `parent_id`, `name`, `text`, `input_caption`, `input_required`, `deleted`) VALUES (5, 0, 'Disgusting', '<html><b><p align=center><span style=\'font-size:20.0pt;color:green\'>Disgusting</span></b>', DEFAULT, DEFAULT, NULL);


-- -----------------------------------------------------
-- Data for table `calendar_out_days`
-- -----------------------------------------------------

INSERT INTO `calendar_out_days` (`id`, `out_day`, `calendar_id`) VALUES (1, '2010-01-01', 1);


-- -----------------------------------------------------
-- Data for table `standards`
-- -----------------------------------------------------

INSERT INTO `standards` (`id`, `wait_max`, `work_max`, `downtime_max`, `line_service_max`, `line_total_max`, `relocation`) VALUES (1, 10, 20, 10, 10, 20, 1);

