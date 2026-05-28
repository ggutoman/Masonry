package org.gag.appdriver.Room.DataObject

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import org.gag.appdriver.Room.Entities.EMemberAddress
import org.gag.appdriver.Room.Entities.EMemberContact
import org.gag.appdriver.Room.Entities.EMemberEmail
import org.gag.appdriver.Room.Entities.EMemberMaster
import org.gag.appdriver.Room.Relations.RMemberFullInfo

@Dao
interface DMember {

    /**
     * Inserts or updates member master information.
     *
     * @param poMember Member master entity.
     */
    @Upsert
    suspend fun saveMember(poMember: EMemberMaster)

    /**
     * Inserts or updates multiple member master records.
     *
     * @param paMember List of member master entities.
     */
    @Upsert
    suspend fun saveMembers(paMember: List<EMemberMaster>)

    /**
     * Retrieves member master information.
     *
     * @param memberId Member ID.
     * @return Member master entity.
     */
    @Query("""
        SELECT * 
        FROM Member_Master 
        WHERE sMemberID = :memberId
    """)
    suspend fun getMember(memberId: String): EMemberMaster?

    /**
     * Retrieves all members.
     *
     * @return List of member master entities.
     */
    @Query("""
        SELECT * 
        FROM Member_Master
        ORDER BY sLastName ASC, sFrstName ASC
    """)
    suspend fun getMembers(): List<EMemberMaster>

    /**
     * Deletes member master information.
     *
     * @param memberId Member ID.
     */
    @Query("""
        DELETE FROM Member_Master 
        WHERE sMemberID = :memberId
    """)
    suspend fun deleteMember(memberId: String)

    /**
     * Deletes all member records.
     */
    @Query("""
        DELETE FROM Member_Master
    """)
    suspend fun deleteAllMembers()

    /**
     * Inserts or updates member address information.
     *
     * @param poAddress Member address entity.
     */
    @Upsert
    suspend fun saveAddress(poAddress: EMemberAddress)

    /**
     * Inserts or updates multiple member addresses.
     *
     * @param paAddress List of member address entities.
     */
    @Upsert
    suspend fun saveAddresses(paAddress: List<EMemberAddress>)

    /**
     * Retrieves member addresses.
     *
     * @param memberId Member ID.
     * @return List of member addresses.
     */
    @Query("""
        SELECT * 
        FROM Member_Address 
        WHERE sMemberID = :memberId
    """)
    suspend fun getAddresses(memberId: String): List<EMemberAddress>

    /**
     * Deletes member addresses.
     *
     * @param memberId Member ID.
     */
    @Query("""
        DELETE FROM Member_Address 
        WHERE sMemberID = :memberId
    """)
    suspend fun deleteAddresses(memberId: String)

    /**
     * Inserts or updates member contact information.
     *
     * @param poContact Member contact entity.
     */
    @Upsert
    suspend fun saveContact(poContact: EMemberContact)

    /**
     * Inserts or updates multiple member contacts.
     *
     * @param paContact List of member contact entities.
     */
    @Upsert
    suspend fun saveContacts(paContact: List<EMemberContact>)

    /**
     * Retrieves member contacts.
     *
     * @param memberId Member ID.
     * @return List of member contacts.
     */
    @Query("""
        SELECT * 
        FROM Member_Contact 
        WHERE sMemberID = :memberId
    """)
    suspend fun getContacts(memberId: String): List<EMemberContact>

    /**
     * Deletes member contacts.
     *
     * @param memberId Member ID.
     */
    @Query("""
        DELETE FROM Member_Contact 
        WHERE sMemberID = :memberId
    """)
    suspend fun deleteContacts(memberId: String)

    /**
     * Inserts or updates member email information.
     *
     * @param poEmail Member email entity.
     */
    @Upsert
    suspend fun saveEmail(poEmail: EMemberEmail)

    /**
     * Inserts or updates multiple member emails.
     *
     * @param paEmail List of member email entities.
     */
    @Upsert
    suspend fun saveEmails(paEmail: List<EMemberEmail>)

    /**
     * Retrieves member emails.
     *
     * @param memberId Member ID.
     * @return List of member email entities.
     */
    @Query("""
        SELECT * 
        FROM Member_Email 
        WHERE sMemberID = :memberId
    """)
    suspend fun getEmails(memberId: String): List<EMemberEmail>

    /**
     * Deletes member emails.
     *
     * @param memberId Member ID.
     */
    @Query("""
        DELETE FROM Member_Email 
        WHERE sMemberID = :memberId
    """)
    suspend fun deleteEmails(memberId: String)

    /**
     * Retrieves complete member information including
     * addresses, contacts, and emails.
     *
     * @param memberId Member ID.
     * @return Complete member information.
     */
    @Transaction
    @Query("""
        SELECT * 
        FROM Member_Master 
        WHERE sMemberID = :memberId
    """)
    suspend fun getMemberFullInfo(memberId: String): RMemberFullInfo?

    /**
     * Retrieves all complete member information.
     *
     * @return List of complete member information.
     */
    @Transaction
    @Query("""
        SELECT * 
        FROM Member_Master
        ORDER BY sLastName ASC, sFrstName ASC
    """)
    suspend fun getMembersFullInfo(): List<RMemberFullInfo>
}