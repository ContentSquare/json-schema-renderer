package com.contentsquare
import io.circe.parser._
import ObjectPropertyDefinition._

object Example extends App {
  val json =
    """
       |{
       |  "$id": "http://schemas.contentsquare.com/elasticsearch/session.json",
       |  "title": "The session",
       |  "type": "object",
       |  "definitions": {
       |    "customVar": {
       |      "$id": "#customVar",
       |      "title": "A custom variable",
       |      "type": "object",
       |      "properties": {
       |        "key": {
       |          "type": "string",
       |          "title": "The custom var key",
       |          "examples": [
       |            "env_template",
       |            "user_logged"
       |          ]
       |        },
       |        "value": {
       |          "type": "string",
       |          "title": "The custom var value",
       |          "examples": [
       |            "funnel_confirmation",
       |            "1"
       |          ]
       |        },
       |        "position": {
       |          "type": "integer",
       |          "title": "The custom var position",
       |          "description": "This is set by Coruscant and represent the 1-based index of the custom var in the original custom vars array",
       |          "examples": [
       |            1,
       |            20
       |          ],
       |          "minimum": 1,
       |          "maximum": 20
       |        }
       |      },
       |      "required": [
       |        "key",
       |        "value",
       |        "position"
       |      ]
       |    },
       |    "geoIp": {
       |      "$id": "#geoIp",
       |      "type": "object",
       |      "title": "The geolocation information",
       |      "description": "Geolocation info based on the IP, injected into the LR1 by Mandalore. This is powered by a legacy version of Max Mind's GeoIP service, so you can find out more about the fields definitions there: https://dev.maxmind.com/geoip/legacy/web-services",
       |      "properties": {
       |        "version": {
       |          "type": "string",
       |          "title": "The version of the component responsible for the geoIp enrichment",
       |          "examples": ["mandalore-0.1"]
       |        },
       |        "ipLocation": {
       |          "type": "object",
       |          "title": "The geolocation information",
       |          "properties": {
       |            "countryCode": {
       |              "type": "string",
       |              "title": "The country code",
       |              "examples": ["FR"]
       |            },
       |            "countryName": {
       |              "type": "string",
       |              "title": "The country name",
       |              "examples": ["France"]
       |            },
       |            "region": {
       |              "type": "string",
       |              "title": "The region"
       |            },
       |            "latitude": {
       |              "type": "number",
       |              "title": "The latitude",
       |              "examples": [48.86000061035156],
       |              "minimum": 0,
       |              "maximum": 90
       |            },
       |            "longitude": {
       |              "type": "number",
       |              "title": "The longitude",
       |              "examples": [2.3499999046325684],
       |              "minimum": -180,
       |              "maximum": 180
       |            },
       |            "timezone": {
       |              "type": "string",
       |              "title": "The timezone",
       |              "examples": ["Europe/Paris"]
       |            },
       |            "postalCode": {
       |              "type": "string",
       |              "title": "The postal code"
       |            },
       |            "dmaCode": {
       |              "type": "integer",
       |              "title": "The USA DMA Code",
       |              "description": "The Nielsen Company Designated Market Area"
       |            },
       |            "areaCode": {
       |              "type": "integer",
       |              "title": "The USA area code"
       |            },
       |            "metroCode": {
       |              "type": "integer",
       |              "title": "The USA metro code"
       |            }
       |          },
       |          "required": [
       |            "countryCode",
       |            "countryName",
       |            "latitude",
       |            "longitude"
       |          ]
       |        }
       |      }
       |    },
       |    "abTestInfo": {
       |      "$id": "#abTestInfo",
       |      "type": "object",
       |      "title": "AB test information",
       |      "properties": {
       |        "testId": {
       |          "type": "integer",
       |          "title": "The internal test ID",
       |          "description": "The test ID in the projects parameters database (internal to CS)",
       |          "example": [2870]
       |        },
       |        "testVersion": {
       |          "type": "integer",
       |          "title": "The internal test version ID",
       |          "description": "The test version ID in the projects parameters database (internal to CS). It should be consistent with the testId, since the testVersion is unique globally.",
       |          "example": [5404]
       |        },
       |        "externalTestId": {
       |          "type": ["string", "null"],
       |          "title": "The external test ID",
       |          "description": "This is always set to null at the moment by Coruscant."
       |        }
       |      },
       |      "required": ["testId", "testVersion"]
       |    }
       |  },
       |  "$schema": "http://json-schema.org/draft-07/schema#",
       |  "properties": {
       |    "sessionKey": {
       |      "type": "string",
       |      "title": "The unique session identifier",
       |      "description": "The unique identifier for the session. Created by Coruscant and equal to SHA1(user ID + session number).",
       |      "examples": [
       |        "247ab29f0e8c0229d251e2e6f530dd7f7ed62d96"
       |      ],
       |      "pattern": "[a-z0-9]{40}"
       |    },
       |    "userId": {
       |      "type": "string",
       |      "title": "The user ID, as passed by the tag in the uu param",
       |      "examples": [
       |        "b0f965b7-28b5-ad12-b0b6-b5625db567da"
       |      ]
       |    },
       |    "projectId": {
       |      "type": "integer",
       |      "title": "The project ID",
       |      "examples": [
       |        88, 912
       |      ],
       |      "exclusiveMinimum": 0
       |    },
       |    "customVarSession": {
       |      "type": "array",
       |      "title": "The session custom variables",
       |      "description": "These custom variables are the custom variables of the last page in the session",
       |      "items": { "$ref": "#customVar" },
       |      "maxItems": 20
       |    },
       |    "recordingEnabled": {
       |      "type": "boolean",
       |      "title": "Whether the recording is enabled for this session.",
       |      "description": "Comes from the 're' param from the tag for a pageview, taken at random. 1 -> true, 0 -> false"
       |    },
       |    "sessionNumber": {
       |      "type": "integer",
       |      "title": "The incremental session number for a given user",
       |      "description": "The ordinal number of the session for a particular user ID. The first visit will be assigned a number 1, and so on.",
       |      "exclusiveMinimum": 0
       |    },
       |    "numberOfPageviews": {
       |      "type": "integer",
       |      "title": "The number of page views in the session",
       |      "exclusiveMinimum": 0
       |    },
       |    "sessionDuration": {
       |      "type": "integer",
       |      "title": "The session duration, in milliseconds ",
       |      "description": "Last page view Kamino time (timestamp) + last event relative time - first page view kamino time",
       |      "minimum": 0
       |    },
       |    "geoIp": {
       |      "$ref": "#geoIp"
       |    },
       |    "firstPageviewAt": {
       |      "type": "integer",
       |      "title": "The epoch time of the first page view, in ms",
       |      "description": "This is the Kamino time, see the pageview schema",
       |      "examples": [1517038659710]
       |    },
       |    "lastPageviewAt": {
       |      "type": "integer",
       |      "title": "The epoch time of the last page view, in ms",
       |      "description": "This is the Kamino time, see the pageview schema",
       |      "examples": [1517040833113]
       |    },
       |    "lastEventAt": {
       |      "type": "integer",
       |      "title": "The epoch time of the last event of the last page view, in ms",
       |      "description": "This is the Kamino time, see the pageview schema",
       |      "examples": [1517060317947]
       |    },
       |    "abTestInfo": {
       |      "type": "array",
       |      "title": "The a/b test information",
       |      "description": "The list of tests and test versions this session is associated with",
       |      "items": { "$ref": "#abTestInfo" }
       |    },
       |    "enrichmentVersion": {
       |      "type": "string",
       |      "title": "The version of the component mainly responsible for the data enrichment",
       |      "examples": ["coruscant-1.0.0"]
       |    },
       |    "browserName": {
       |      "type": "string",
       |      "title": "The browser name",
       |      "description": "This information comes from the ua param sent by the tag, identified by this library: https://www.bitwalker.eu/software/user-agent-utils, and then further processed by Coruscant.",
       |      "examples": ["Safari"]
       |    },
       |    "browserVersion": {
       |      "type": "string",
       |      "title": "The browser version ",
       |      "description": "This information comes from the ua param sent by the tag, identified by this library: https://www.bitwalker.eu/software/user-agent-utils, and then further processed by Coruscant.",
       |      "examples": ["10.1.2"]
       |    },
       |    "deviceType": {
       |      "type": "string",
       |      "title": "The device type",
       |      "description": "This information comes from the ua param sent by the tag, identified by this library: https://www.bitwalker.eu/software/user-agent-utils, and then further processed by Coruscant.",
       |      "enum": [
       |        "Computer",
       |        "Mobile",
       |        "Tablet",
       |        "Game console",
       |        "Digital media receiver",
       |        "Wearable computer",
       |        "Unknown",
       |        "unknown"
       |      ]
       |    },
       |    "platformName": {
       |      "type": "string",
       |      "title": "The OS name ",
       |      "description": "This information comes from the ua param sent by the tag, identified by this library: https://www.bitwalker.eu/software/user-agent-utils, and then further processed by Coruscant.",
       |      "examples": ["Mac OS X"]
       |    },
       |    "deviceManufacturer": {
       |      "type": "string",
       |      "title": "The device manufacturer ",
       |      "description": "This information comes from the ua param sent by the tag, identified by this library: https://www.bitwalker.eu/software/user-agent-utils, and then further processed by Coruscant.",
       |      "examples": ["Apple Inc."]
       |    },
       |    "userLanguage": {
       |      "type": "string",
       |      "title": "The user language",
       |      "descurption": "The most frequent language used in the session's pageviews, as found in the ua param in the tag",
       |      "examples": ["fr-FR"]
       |    },
       |    "inSitePath": {
       |      "type": "string",
       |      "title": "The compressed inSitePath data",
       |      "description": "The compressed inSitePath data (see inSitePathFull), limited to 21 page views",
       |      "examples": [
       |        "H4sIAAAAAAAAAI2PTQvCMAyG/8qOnuymInM30R0EP8B5lVK6bCusH3Rx4MXfblY/0IMghJA3edK3\r\naRBdlzFWetXDWBkEr4WXDYyl1WxlTaWogcoaFs8mSRrH6ZyNzjcwPUfQrhUIJKuLMdBy+cFT14ka\r\nuBF6IACFAz7NsmfxxZLQWphyAC8deN7auoaSVPKvV4fWA5cKrySK5WZ/inb59rA5Ruu8iA57yjR4\r\nxIMln17J4cVw/Ou/UuBgisHlvfIrwkoLfUCTxR12JN1DTwEAAA==\r\n"
       |      ]
       |    },
       |    "inSitePathFull": {
       |      "type": "string",
       |      "title": "The compressed full inSitePath data ",
       |      "description": "The base64 encoded, gzipped concatenation of all page views path info in the sessions, where a path info is the concatenation of the URL and the customVars. Roughly: Base64(gzip(concat(pageviews.map(url + concat(cvars))))). This transformation is done by Coruscant",
       |      "examples": [
       |        "H4sIAAAAAAAAAI2PTQvCMAyG/8qOnuymInM30R0EP8B5lVK6bCusH3Rx4MXfblY/0IMghJA3edK3\r\naRBdlzFWetXDWBkEr4WXDYyl1WxlTaWogcoaFs8mSRrH6ZyNzjcwPUfQrhUIJKuLMdBy+cFT14ka\r\nuBF6IACFAz7NsmfxxZLQWphyAC8deN7auoaSVPKvV4fWA5cKrySK5WZ/inb59rA5Ruu8iA57yjR4\r\nxIMln17J4cVw/Ou/UuBgisHlvfIrwkoLfUCTxR12JN1DTwEAAA==\r\n"
       |      ]
       |    },
       |    "randomScore": {
       |      "type": "number",
       |      "title": "A random score",
       |      "description": "This random score is set at ingestion time in order to sample the data randomly, in a deterministic way",
       |      "examples": [0.7664093375205994],
       |      "minimum": 0,
       |      "maximum": 1
       |    }
       |  },
       |  "required": [
       |    "sessionKey",
       |    "userId",
       |    "projectId",
       |    "customVarSession",
       |    "recordingEnabled",
       |    "sessionNumber",
       |    "numberOfPageviews",
       |    "sessionDuration",
       |    "geoIp",
       |    "firstPageviewAt",
       |    "lastPageviewAt",
       |    "lastEventAt",
       |    "abTestInfo",
       |    "enrichmentVersion",
       |    "browserName",
       |    "browserVersion",
       |    "deviceType",
       |    "platformName",
       |    "deviceManufacturer",
       |    "userLanguage",
       |    "inSitePath",
       |    "inSitePathFull",
       |    "randomScore"
       |  ]
       |}
    """.stripMargin

  val objectJson =
    """
      |{
      |      "$id": "#customVar",
      |      "title": "bla",
      |      "type": "object",
      |      "properties": {
      |        "key": {
      |          "type": "string",
      |          "title": "The custom var key",
      |          "examples": [
      |            "env_template",
      |            "user_logged"
      |          ]
      |        },
      |        "value": {
      |          "type": "string",
      |          "title": "The custom var value",
      |          "examples": [
      |            "funnel_confirmation",
      |            "1"
      |          ]
      |        },
      |        "position": {
      |          "type": "integer",
      |          "title": "The custom var position",
      |          "description": "This is set by Coruscant and represent the 1-based index of the custom var in the original custom vars array",
      |          "examples": [
      |            1,
      |            20
      |          ],
      |          "minimum": 1,
      |          "maximum": 20
      |        }
      |      },
      |      "required": [
      |        "key",
      |        "value",
      |        "position"
      |      ]
      |    }
    """.stripMargin

  println(decode[ObjectPropertyDefinition](json))
}
