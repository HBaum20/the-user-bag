package io.baum.userbagservice.users.tools

import io.baum.userbagservice.users.kafka.UserRecord
import io.baum.userbagservice.users.web.model.UserModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ExtensionsTest {

    @Nested
    inner class BasicConversion {

        private val model = UserModel(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714282896",
                "password"
        )

        private val record = UserRecord(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714282896",
                "password"
        )

        @Test
        fun shouldConvertUserModelToUserRecord() {
            val subject = model.toKafkaRecord()

            assertThat(subject).isInstanceOf(UserRecord::class.java)
            assertThat(subject).isEqualToComparingFieldByField(record)
        }

        @Test
        fun shouldConvertUserRecordToUserModel() {
            val subject = record.toDomain()

            assertThat(subject).isInstanceOf(UserModel::class.java)
            assertThat(subject).isEqualToComparingFieldByField(record)
        }
    }

    @Nested
    inner class ModelManipulation {
        private val model = UserModel(
                "1",
                "Arthur Morgan",
                8000.0,
                "arthur.morgan@skybettingandgaming.com",
                "07714282896",
                "password"
        )

        private val censoredModel = UserModel(
                "1",
                "Arthur Morgan",
                -1.0,
                "*************************************",
                "***********",
                "********"
        )

        @Test
        fun asteriskizeShouldReplaceAllCharsWithAsterisks() {
            val str = "articulate"
            val aStr = str.asteriskize()

            assertThat(aStr).isEqualTo("**********")
        }

        @Test
        fun hideSensitiveFieldsShouldCensorFieldsWithAsterisks() {
            val subject = model.hideSensitiveFields()

            assertThat(subject).isEqualToComparingFieldByField(censoredModel)
        }
    }
}