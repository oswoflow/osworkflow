# OSWORKFLOW #

Fork from opensymphony-osworkflow

### Features added ###

- add mariadb/mysql support

- JDK 8

### build ###

$> mvn clean install


### example xml workflow ###

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE workflow PUBLIC "-//OpenSymphony Group//DTD OSWorkflow 2.8//EN" "https://raw.githubusercontent.com/ailohq/osworkflow/master/src/main/resources/workflow_2_8.dtd">
<workflow>
    <initial-actions>
        <action id="1" name="Iniciar Fluxo">
            <results>
                <unconditional-result old-status="none" status="Submetido" step="1"/>
            </results>
        </action>
    </initial-actions>
    <steps>
        <step id="1" name="Submetido">
            <actions>
                <action id="2" name="Enviar para Revisão">
                    <results>
                        <unconditional-result old-status="Submetido" status="Em Revisão" step="2"/>
                    </results>
                </action>
            </actions>
        </step>
        <step id="2" name="Em Revisão">
            <actions>
                <action id="3" name="Aprovar Documento">
                    <results>
                        <unconditional-result old-status="Em Revisão" status="Aprovado" step="3"/>
                    </results>
                </action>
                <action id="4" name="Rejeitar Documento">
                    <results>
                        <unconditional-result old-status="Em Revisão" status="Rejeitado" step="4"/>
                    </results>
                </action>
            </actions>
        </step>
        <step id="3" name="Aprovado">
            <actions>
                <action id="5" name="Finalizar" finish="true">
                    <results>
                        <unconditional-result old-status="Aprovado" status="Finalizado" step="3"/>
                    </results>
                </action>
            </actions>
        </step>
        <step id="4" name="Rejeitado">
            <actions>
                <action id="6" name="Reenviar para Revisão">
                    <results>
                        <unconditional-result old-status="Rejeitado" status="Submetido" step="1"/>
                    </results>
                </action>
            </actions>
        </step>
    </steps>
</workflow>
```

### example configuration ###

create osworkflow.xml on resources

```xml
<?xml version="1.0" encoding="UTF-8"?>
<osworkflow>
</osworkflow>
```

#### kotlin bean config ####

```kotlin
@Configuration
class WorkflowConfig {
    @Bean
    fun workflow(dataSource: DataSource): Workflow {
        val store = JDBCWorkflowStore()
        store.init(emptyMap<String, Any>())
        store.ds = dataSource
        val workflow = BasicWorkflow("default_user")
        val factory = JDBCWorkflowFactory()
        val factoryProp = Properties()
        factoryProp.setProperty("reload", "true")
        factory.properties = factoryProp
        factory.dataSource = dataSource
        factory.initDone()
        val config = SpringConfiguration()
        config.setStore(store)
        config.setFactory(factory)
        workflow.configuration = config
        return workflow
    }

    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}
```


#### workflow db model ####

```mysql

-- WORKFLOW

-- Table structure `OS_CURRENTSTEP`
CREATE TABLE `OS_CURRENTSTEP` (
  `ID` bigint(20) AUTO_INCREMENT,
  `ENTRY_ID` bigint(20) DEFAULT NULL,
  `STEP_ID` int(11) DEFAULT NULL,
  `ACTION_ID` int(11) DEFAULT NULL,
  `OWNER` varchar(35) DEFAULT NULL,
  `START_DATE` datetime DEFAULT NULL,
  `FINISH_DATE` datetime DEFAULT NULL,
  `DUE_DATE` datetime DEFAULT NULL,
  `STATUS` varchar(40) DEFAULT NULL,
  `CALLER` varchar(35) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `ENTRY_ID` (`ENTRY_ID`),
  KEY `OWNER` (`OWNER`),
  KEY `CALLER` (`CALLER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `OS_CURRENTSTEP_PREV`
CREATE TABLE `OS_CURRENTSTEP_PREV` (
  `ID` bigint(20) AUTO_INCREMENT,
  `PREVIOUS_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`, `PREVIOUS_ID`),
  KEY `ID` (`ID`),
  KEY `PREVIOUS_ID` (`PREVIOUS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `OS_ENTRYIDS`
CREATE TABLE `OS_ENTRYIDS` (
  `ID` bigint(20) AUTO_INCREMENT,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=2;

-- --------------------------------------------------------

-- Table structure `OS_GROUP`
CREATE TABLE `OS_GROUP` (
  `GROUPNAME` varchar(20) NOT NULL,
  PRIMARY KEY (`GROUPNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `OS_HISTORYSTEP`
CREATE TABLE `OS_HISTORYSTEP` (
  `ID` bigint(20) AUTO_INCREMENT,
  `ENTRY_ID` bigint(20) DEFAULT NULL,
  `STEP_ID` int(11) DEFAULT NULL,
  `ACTION_ID` int(11) DEFAULT NULL,
  `OWNER` varchar(35) DEFAULT NULL,
  `START_DATE` datetime DEFAULT NULL,
  `FINISH_DATE` datetime DEFAULT NULL,
  `DUE_DATE` datetime DEFAULT NULL,
  `STATUS` varchar(40) DEFAULT NULL,
  `CALLER` varchar(35) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `ENTRY_ID` (`ENTRY_ID`),
  KEY `OWNER` (`OWNER`),
  KEY `CALLER` (`CALLER`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `OS_HISTORYSTEP_PREV`
CREATE TABLE `OS_HISTORYSTEP_PREV` (
  `ID` bigint(20) AUTO_INCREMENT,
  `PREVIOUS_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`, `PREVIOUS_ID`),
  KEY `ID` (`ID`),
  KEY `PREVIOUS_ID` (`PREVIOUS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `OS_MEMBERSHIP`
CREATE TABLE `OS_MEMBERSHIP` (
  `USERNAME` varchar(20) NOT NULL,
  `GROUPNAME` varchar(20) NOT NULL,
  PRIMARY KEY (`USERNAME`, `GROUPNAME`),
  KEY `USERNAME` (`USERNAME`),
  KEY `GROUPNAME` (`GROUPNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `OS_PROPERTYENTRY`
CREATE TABLE `OS_PROPERTYENTRY` (
  `GLOBAL_KEY` varchar(250) NOT NULL,
  `ITEM_KEY` varchar(250) NOT NULL,
  `ITEM_TYPE` tinyint(4) DEFAULT NULL,
  `STRING_VALUE` varchar(255) DEFAULT NULL,
  `DATE_VALUE` datetime DEFAULT NULL,
  `DATA_VALUE` longtext,
  `FLOAT_VALUE` float DEFAULT NULL,
  `NUMBER_VALUE` decimal(10,0) DEFAULT NULL,
  PRIMARY KEY (`GLOBAL_KEY`, `ITEM_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `OS_USER`
CREATE TABLE `OS_USER` (
  `USERNAME` varchar(100) NOT NULL,
  `PASSWORDHASH` mediumtext,
  PRIMARY KEY (`USERNAME`),
  KEY `USERNAME` (`USERNAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `OS_WFENTRY`
CREATE TABLE `OS_WFENTRY` (
  `ID` bigint(20) AUTO_INCREMENT,
  `NAME` varchar(60) DEFAULT NULL,
  `STATE` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `OS_WORKFLOWDEFS`
CREATE TABLE `OS_WORKFLOWDEFS` (
  `WF_NAME` varchar(250) NOT NULL,
  `WF_DEFINITION` longtext NOT NULL,
  PRIMARY KEY (`WF_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `SL_CONFIG`
CREATE TABLE `SL_CONFIG` (
  `KEY` varchar(64) NOT NULL,
  `TYPE` int(11) NOT NULL,
  `DESCRIPTION` varchar(256) DEFAULT NULL,
  `VALUE` varchar(256) NOT NULL,
  `DEFAULT_VALUE` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

-- Table structure `SL_MESSAGES`
CREATE TABLE `SL_MESSAGES` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `type` varchar(256) NOT NULL,
  `content` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1;
```