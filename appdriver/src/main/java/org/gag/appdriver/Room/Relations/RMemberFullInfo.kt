package org.gag.appdriver.Room.Relations

import androidx.room.Embedded
import androidx.room.Relation
import org.gag.appdriver.Room.Entities.EMemberAddress
import org.gag.appdriver.Room.Entities.EMemberContact
import org.gag.appdriver.Room.Entities.EMemberEmail
import org.gag.appdriver.Room.Entities.EMemberInfo

/**
 * Complete Member Information Relation
 *
 * This class loads a Member together with:
 * - Addresses
 * - Contacts
 * - Emails
 *
 * One-to-many relationships based on sMemberID
 */
data class RMemberFullInfo(

    /**
     * Main member information (parent table)
     */
    @Embedded
    val member: EMemberInfo,

    /**
     * Member addresses (child table)
     */
    @Relation(
        parentColumn = "sMemberID",
        entityColumn = "sMemberID"
    )
    val addresses: List<EMemberAddress>,

    /**
     * Member contacts (child table)
     */
    @Relation(
        parentColumn = "sMemberID",
        entityColumn = "sMemberID"
    )
    val contacts: List<EMemberContact>,

    /**
     * Member emails (child table)
     */
    @Relation(
        parentColumn = "sMemberID",
        entityColumn = "sMemberID"
    )
    val emails: List<EMemberEmail>
)