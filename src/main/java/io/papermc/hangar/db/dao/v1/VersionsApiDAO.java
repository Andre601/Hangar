package io.papermc.hangar.db.dao.v1;

import io.papermc.hangar.db.mappers.TagMapper;
import io.papermc.hangar.model.api.project.version.PluginDependency;
import io.papermc.hangar.model.api.project.version.Tag;
import io.papermc.hangar.model.api.project.version.Version;
import io.papermc.hangar.model.api.project.version.VersionStats;
import io.papermc.hangar.model.common.Platform;
import org.jdbi.v3.core.enums.EnumByOrdinal;
import org.jdbi.v3.core.enums.EnumStrategy;
import org.jdbi.v3.sqlobject.config.KeyColumn;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.config.UseEnumStrategy;
import org.jdbi.v3.sqlobject.config.ValueColumn;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.stringtemplate4.UseStringTemplateEngine;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

@Repository
@UseStringTemplateEngine
@UseEnumStrategy(EnumStrategy.BY_ORDINAL)
@RegisterConstructorMapper(Version.class)
public interface VersionsApiDAO {

    @KeyColumn("id")
    @SqlQuery("SELECT pv.id," +
            "       pv.created_at," +
            "       pv.version_string," +
            "       pv.visibility," +
            "       pv.description," +
            "       coalesce((SELECT sum(pvd.downloads) FROM project_versions_downloads pvd WHERE p.id = pvd.project_id AND pv.id = pvd.version_id), 0) vs_downloads," +
            "       pv.file_name fi_name," +
            "       pv.file_size fi_size_bytes," +
            "       pv.hash fi_md5_hash," +
            "       pv.external_url," +
            "       u.name author," +
            "       pv.review_state," +
            "       exists(SELECT 1 FROM recommended_project_versions rpv WHERE rpv.version_id = pv.id) as recommended" +
            "   FROM project_versions pv" +
            "       JOIN projects p ON pv.project_id = p.id" +
            "       LEFT JOIN users u ON pv.author_id = u.id" +
            "   WHERE " +
            "       <if(!canSeeHidden)>" +
            "           (pv.visibility = 0 " +
            "           <if(userId)>" +
            "               OR (<userId> IN (SELECT pm.user_id FROM project_members_all pm WHERE pm.id = p.id) AND pv.visibility != 4) " +
            "           <endif>)" +
            "           AND" +
            "       <endif>" +
            "       pv.id = :versionId" +
            "   ORDERED BY pv.created_at DESC"
    )
    Entry<Long, Version> getVersion(long versionId, @Define boolean canSeeHidden, @Define Long userId);

    @KeyColumn("id")
    @SqlQuery("SELECT pv.id," +
            "       pv.created_at," +
            "       pv.version_string," +
            "       pv.visibility," +
            "       pv.description," +
            "       coalesce((SELECT sum(pvd.downloads) FROM project_versions_downloads pvd WHERE p.id = pvd.project_id AND pv.id = pvd.version_id), 0) vs_downloads," +
            "       pv.file_name fi_name," +
            "       pv.file_size fi_size_bytes," +
            "       pv.hash fi_md5_hash," +
            "       pv.external_url," +
            "       u.name author," +
            "       pv.review_state," +
            "       exists(SELECT 1 FROM recommended_project_versions rpv WHERE rpv.version_id = pv.id) as recommended" +
            "   FROM project_versions pv" +
            "       JOIN projects p ON pv.project_id = p.id" +
            "       LEFT JOIN users u ON pv.author_id = u.id" +
            "   WHERE " +
            "       <if(!canSeeHidden)>" +
            "           (pv.visibility = 0 " +
            "           <if(userId)>" +
            "               OR (<userId> IN (SELECT pm.user_id FROM project_members_all pm WHERE pm.id = p.id) AND pv.visibility != 4) " +
            "           <endif>)" +
            "           AND" +
            "       <endif>" +
            "       lower(p.owner_name) = lower(:author) AND" +
            "       lower(p.slug) = lower(:slug) AND" +
            "       pv.version_string = :versionString" +
            "   ORDER BY pv.created_at DESC"
    )
    SortedMap<Long, Version> getVersionsWithVersionString(String author, String slug, String versionString, @Define boolean canSeeHidden, @Define Long userId);

    @KeyColumn("id")
    @SqlQuery("SELECT pv.id," +
            "       pv.created_at," +
            "       pv.version_string," +
            "       pv.visibility," +
            "       pv.description," +
            "       coalesce((SELECT sum(pvd.downloads) FROM project_versions_downloads pvd WHERE p.id = pvd.project_id AND pv.id = pvd.version_id), 0) vs_downloads," +
            "       pv.file_name fi_name," +
            "       pv.file_size fi_size_bytes," +
            "       pv.hash fi_md5_hash," +
            "       pv.external_url," +
            "       u.name author," +
            "       pv.review_state," +
            "       exists(SELECT 1 FROM recommended_project_versions rpv WHERE rpv.version_id = pv.id) as recommended" +
            "   FROM project_versions pv" +
            "   JOIN projects p ON pv.project_id = p.id" +
            "   LEFT JOIN users u ON pv.author_id = u.id" +
            "   WHERE " +
            "       <if(!canSeeHidden)>" +
            "           (pv.visibility = 0 " +
            "           <if(userId)>" +
            "               OR (<userId> IN (SELECT pm.user_id FROM project_members_all pm WHERE pm.id = p.id) AND pv.visibility != 4) " +
            "           <endif>)" +
            "           AND" +
            "       <endif>" +
            "       lower(p.owner_name) = lower(:author) AND" +
            "       lower(p.slug) = lower(:slug) " +
            // TODO tags
            /*"       <if(tags)> AND (" +
            "           pvt.name || ':' || pvt.data IN (<tags>) OR " +
            "           pvt.name IN (<tags>) OR " +
            "           'Channel:' || pc.name IN (<tags>) OR " +
            "           'Channel' IN (<tags>)" +
            "           )" +
            "       <endif> " +*/
            "   ORDER BY pv.created_at DESC LIMIT :limit OFFSET :offset"
    )
    SortedMap<Long, Version> getVersions(String author, String slug, @BindList(onEmpty = BindList.EmptyHandling.NULL_VALUE) List<String> tags, @Define boolean canSeeHidden, @Define Long userId, long limit, long offset);

    @KeyColumn("platform")
    @SqlQuery("SELECT " +
            "       pvd.name," +
            "       pvd.required," +
            "       pvd.external_url," +
            "       p.owner_name pn_owner," +
            "       p.slug pn_slug" +
            "   FROM project_version_dependencies pvd" +
            "       LEFT JOIN projects p ON pvd.project_id = p.id" +
            "   WHERE pvd.version_id = :versionId AND pvd.platform = :platform")
    @RegisterConstructorMapper(PluginDependency.class)
    Set<PluginDependency> getPluginDependencies(long versionId, @EnumByOrdinal Platform platform);

    @KeyColumn("platform")
    @ValueColumn("versions")
    @SqlQuery("SELECT" +
            "       pv.platform," +
            "       array_agg(pv.version ORDER BY pv.created_at) versions" +
            "   FROM project_version_platform_dependencies pvpd " +
            "       JOIN platform_versions pv ON pvpd.platform_version_id = pv.id" +
            "   WHERE pvpd.version_id = :versionId" +
            "   GROUP BY pv.platform")
    Map<Platform, SortedSet<String>> getPlatformDependencies(long versionId);

    @SqlQuery("SELECT pvt.name, pvt.data, pvt.color FROM project_version_tags pvt WHERE pvt.version_id = :versionId")
    @RegisterRowMapper(TagMapper.class)
    Set<Tag> getVersionTags(long versionId);

    /*@UseRowReducer(VersionReducer.class)
    @RegisterConstructorMapper(value = PluginDependency.class, prefix = "pd_")
    @UseStringTemplateEngine
    @SqlQuery("SELECT pv.created_at," +
            "         pv.version_string," +
            "         pv.visibility," +
            "         pv.description," +
            "         coalesce((SELECT sum(pvd.downloads) FROM project_versions_downloads pvd WHERE p.id = pvd.project_id AND pv.id = pvd.version_id), 0) vs_downloads," +
            "         pv.file_name fi_name," +
            "         pv.file_size fi_size_bytes," +
            "         pv.hash fi_md5_hash," +
            "         pv.external_url," +
            "         u.name author," +
            "         pv.review_state," +
            "         pvt.name AS tag_name," +
            "         pvt.data AS tag_data," +
            "         pvt.color AS tag_color," +
            "         'Channel' AS ch_tag_name," +
            "         pc.name AS ch_tag_data," +
            "         pc.color AS ch_tag_color," +
            "         d.platform pd_platform," +
            "         d.name pd_name," +
            "         d.required pd_required," +
            "         pdp.owner_name pd_owner," +
            "         pdp.slug pd_slug," +
            "         d.external_url pd_external_url," +
            "         plv.platform p_platform," +
            "         plv.version p_version," +
            "         exists(SELECT 1 FROM recommended_project_versions rpv WHERE rpv.version_id = pv.id) as recommended" +
            "   FROM project_versions pv" +
            "       JOIN projects p ON pv.project_id = p.id" +
            "       LEFT JOIN users u ON pv.author_id = u.id" +
            "       LEFT JOIN project_version_tags pvt ON pv.id = pvt.version_id" +
            "       LEFT JOIN project_channels pc ON pv.channel_id = pc.id " +
            "       JOIN project_version_platform_dependencies pvpd ON pv.id = pvpd.version_id" +
            "       JOIN platform_versions plv ON pvpd.platform_version_id = plv.id" +
            "       LEFT JOIN project_version_dependencies d ON pv.id = d.version_id" +
            "       LEFT JOIN projects pdp ON d.project_id = pdp.id" +
            "   WHERE <if(!canSeeHidden)>(pv.visibility = 0 " +
            "       <if(userId)>OR (<userId> IN (SELECT pm.user_id FROM project_members_all pm WHERE pm.id = p.id) AND pv.visibility != 4) <endif>) AND <endif> " +
            "       plv.platform = :platform AND" +
            "       pvt.name IS NOT NULL AND" +
            "       lower(p.owner_name) = lower(:author) AND" +
            "       lower(p.slug) = lower(:slug) AND" +
            "       lower(pv.version_string) = lower(:versionString)" +
            "   GROUP BY p.id, pv.id, u.id, pc.id, d.id, plv.id, pvt.id, pdp.id" +
            "   ORDER BY pv.created_at DESC")
    Version getVersion(String author, String slug, String versionString, @EnumByOrdinal Platform platform, @Define boolean canSeeHidden, @Define Long userId);

    @UseEnumStrategy(EnumStrategy.BY_ORDINAL)
    @UseRowReducer(VersionReducer.class)
    @RegisterConstructorMapper(value = PluginDependency.class, prefix = "pd_")
    @UseStringTemplateEngine
    @SqlQuery("SELECT pv.id," +
            "         pv.created_at," +
            "         pv.version_string," +
            "         pv.visibility," +
            "         pv.description," +
            "         coalesce((SELECT sum(pvd.downloads) FROM project_versions_downloads pvd WHERE p.id = pvd.project_id AND pv.id = pvd.version_id), 0) vs_downloads," +
            "         pv.file_name fi_name," +
            "         pv.file_size fi_size_bytes," +
            "         pv.hash fi_md5_hash," +
            "         pv.external_url," +
            "         u.name author," +
            "         pv.review_state," +
            "         pvt.name AS tag_name," +
            "         pvt.data AS tag_data," +
            "         pvt.color AS tag_color," +
            "         'Channel' AS ch_tag_name," +
            "         pc.name AS ch_tag_data," +
            "         pc.color AS ch_tag_color," +
            "         d.platform pd_platform," +
            "         d.name pd_name," +
            "         d.required pd_required," +
            "         pdp.owner_name pd_owner," +
            "         pdp.slug pd_slug," +
            "         d.external_url pd_external_url," +
            "         plv.platform p_platform," +
            "         plv.version p_version," +
            "         exists(SELECT 1 FROM recommended_project_versions rpv WHERE rpv.version_id = pv.id) as recommended" +
            "   FROM project_versions pv" +
            "       JOIN projects p ON pv.project_id = p.id" +
            "       LEFT JOIN users u ON pv.author_id = u.id" +
            "       LEFT JOIN project_version_tags pvt ON pv.id = pvt.version_id" +
            "       LEFT JOIN project_channels pc ON pv.channel_id = pc.id " +
            "       JOIN project_version_platform_dependencies pvpd ON pv.id = pvpd.version_id" +
            "       JOIN platform_versions plv ON pvpd.platform_version_id = plv.id" +
            "       LEFT JOIN project_version_dependencies d ON pv.id = d.version_id" +
            "       LEFT JOIN projects pdp ON d.project_id = pdp.id" +
            "   WHERE <if(!canSeeHidden)>(pv.visibility = 0 " +
            "       <if(userId)>OR (<userId> IN (SELECT pm.user_id FROM project_members_all pm WHERE pm.id = p.id) AND pv.visibility != 4) <endif>) AND <endif> " +
            "       pvt.name IS NOT NULL AND" +
            "       lower(p.owner_name) = lower(:author) AND" +
            "       lower(p.slug) = lower(:slug) AND" +
            "       lower(pv.version_string) = lower(:versionString)" +
            "   GROUP BY p.id, pv.id, u.id, pc.id, d.id, plv.id, pvt.id, pdp.id" +
            "   ORDER BY pv.created_at DESC")
    List<Version> getVersions(String author, String slug, String versionString, @Define boolean canSeeHidden, @Define Long userId);



    @UseEnumStrategy(EnumStrategy.BY_ORDINAL)
    @UseRowReducer(VersionReducer.class)
    @RegisterConstructorMapper(value = PluginDependency.class, prefix = "pd_")
    @UseStringTemplateEngine
    @SqlQuery("SELECT pv.id," +
            "         pv.created_at," +
            "         pv.version_string," +
            "         pv.visibility," +
            "         pv.description," +
            "         coalesce((SELECT sum(pvd.downloads) FROM project_versions_downloads pvd WHERE p.id = pvd.project_id AND pv.id = pvd.version_id), 0) vs_downloads," +
            "         pv.file_name fi_name," +
            "         pv.file_size fi_size_bytes," +
            "         pv.hash fi_md5_hash," +
            "         pv.external_url," +
            "         u.name author," +
            "         pv.review_state," +
            "         pvt.name AS tag_name," +
            "         pvt.data AS tag_data," +
            "         pvt.color AS tag_color," +
            "         'Channel' AS ch_tag_name," +
            "         pc.name AS ch_tag_data," +
            "         pc.color AS ch_tag_color," +
            "         d.platform pd_platform," +
            "         d.name pd_name," +
            "         d.required pd_required," +
            "         pdp.owner_name pd_owner," +
            "         pdp.slug pd_slug," +
            "         d.external_url pd_external_url," +
            "         plv.platform p_platform," +
            "         plv.version p_version," +
            "         exists(SELECT 1 FROM recommended_project_versions rpv WHERE rpv.version_id = pv.id) as recommended," +
            "         ru.name approved_by" +
            "   FROM project_versions pv" +
            "       JOIN projects p ON pv.project_id = p.id" +
            "       LEFT JOIN users u ON pv.author_id = u.id" +
            "       LEFT JOIN project_version_tags pvt ON pv.id = pvt.version_id" +
            "       LEFT JOIN project_channels pc ON pv.channel_id = pc.id " +
            "       JOIN project_version_platform_dependencies pvpd ON pv.id = pvpd.version_id" +
            "       JOIN platform_versions plv ON pvpd.platform_version_id = plv.id" +
            "       LEFT JOIN project_version_dependencies d ON pv.id = d.version_id" +
            "       LEFT JOIN projects pdp ON d.project_id = pdp.id" +
            "       LEFT JOIN users ru ON pv.reviewer_id = ru.id" +
            "   WHERE <if(!canSeeHidden)>(pv.visibility = 0 " +
            "       <if(userId)>OR (<userId> IN (SELECT pm.user_id FROM project_members_all pm WHERE pm.id = p.id) AND pv.visibility != 4) <endif>) AND <endif> " +
            "       pvt.name IS NOT NULL AND" +
            "       lower(p.owner_name) = lower(:author) AND" +
            "       lower(p.slug) = lower(:slug)" +
            "   GROUP BY p.id, pv.id, u.id, pc.id, d.id, plv.id, pvt.id, pdp.id, ru.id" +
            "   ORDER BY pv.created_at DESC LIMIT :limit OFFSET :offset")
    List<Version> getVersions(String author, String slug, @BindList(onEmpty = BindList.EmptyHandling.NULL_VALUE) List<String> tags, @Define boolean canSeeHidden, @Define Long userId, long limit, long offset);*/

    @SqlQuery("SELECT COUNT(*) " +
            "FROM projects p" +
            "   JOIN project_versions pv ON p.id = pv.project_id" +
            "   LEFT JOIN users u ON pv.author_id = u.id " +
//            "   LEFT JOIN project_version_tags pvt ON pv.id = pvt.version_id" +
//            "   LEFT JOIN project_channels pc ON pv.channel_id = pc.id " +
            "WHERE <if(!canSeeHidden)>(pv.visibility = 0 " +
            "<if(userId)>OR (<userId> IN (SELECT pm.user_id FROM project_members_all pm WHERE pm.id = p.id) AND pv.visibility != 4) <endif>) AND <endif> " +
            "lower(p.slug) = lower(:slug) AND " +
            "lower(p.owner_name) = lower(:author)" + /* <if(tags)> AND " +
            // TODO tags
            "(" +
            "   pvt.name || ':' || pvt.data IN (<tags>) OR " +
            "   pvt.name IN (<tags>) OR " +
            "   'Channel:' || pc.name IN (<tags>) OR " +
            "   'Channel' IN (<tags>)" +
            "  )<endif> " + */
            "GROUP BY p.id, pv.id, u.id")
    Long getVersionCount(String author, String slug, @BindList(onEmpty = BindList.EmptyHandling.NULL_VALUE) List<String> tags, @Define boolean canSeeHidden, @Define Long userId);

    // TODO this might be totally screwed up by adding the platform check
    @KeyColumn("date")
    @RegisterConstructorMapper(value = VersionStats.class, prefix = "vs")
    @SqlQuery("SELECT CAST(dates.day as DATE) date, coalesce(pvd.downloads, 0) vs_downloads" +
            "    FROM projects p," +
            "         project_versions pv" +
            "           JOIN project_version_platform_dependencies pvpd ON pv.id = pvpd.version_id" +
            "           JOIN platform_versions plv ON pvpd.platform_version_id = plv.id," +
            "         (SELECT generate_series(:fromDate::DATE, :toDate::DATE, INTERVAL '1 DAY') AS day) dates" +
            "             LEFT JOIN project_versions_downloads pvd ON dates.day = pvd.day" +
            "    WHERE p.owner_name = :author" +
            "      AND p.slug = :slug" +
            "      AND pv.version_string = :versionString" +
            "      AND plv.platform = :platform" +
            "      AND (pvd IS NULL OR (pvd.project_id = p.id AND pvd.version_id = pv.id));")
    Map<String, VersionStats> getVersionStats(String author, String slug, String versionString, @EnumByOrdinal Platform platform, OffsetDateTime fromDate, OffsetDateTime toDate);

    /*class VersionReducer implements LinkedHashMapRowReducer<Long, Version> {
        @Override
        public void accumulate(Map<Long, Version> container, RowView rowView) {
            final Version version = container.computeIfAbsent(rowView.getColumn("id", Long.class), id -> rowView.getRow(Version.class));
            VersionReducer._accumulateVersion(rowView, version);
        }

        public static <T extends Version> void _accumulateVersion(RowView rowView, T version) { // What a mess really
            Platform pluginPlatform = rowView.getColumn("pd_platform", Platform.class);
            if (pluginPlatform != null) {
                version.getPluginDependencies().computeIfAbsent(pluginPlatform, _pl -> new HashSet<>());
                version.getPluginDependencies().get(pluginPlatform).add(rowView.getRow(PluginDependency.class));
            }

            Platform platformPlatform = rowView.getColumn("p_platform", Platform.class);
            version.getPlatformDependencies().computeIfAbsent(platformPlatform, _pl -> new HashSet<>());
            version.getPlatformDependencies().get(platformPlatform).add(rowView.getColumn("p_version", String.class));

            if (rowView.getColumn("ch_tag_name", String.class) != null) {
                version.getTags().add(new Tag(rowView.getColumn("ch_tag_name", String.class), rowView.getColumn("ch_tag_data", String.class), new TagColor(null, rowView.getColumn("ch_tag_color", Color.class).getHex())));
            }

            if (rowView.getColumn("tag_name", String.class) != null) {
                version.getTags().add(new Tag(
                        rowView.getColumn("tag_name", String.class),
                        StringUtils.formatVersionNumbers(Arrays.asList(rowView.getColumn("tag_data", String[].class))),
                        rowView.getColumn("tag_color", io.papermc.hangar.model.common.TagColor.class).toTagColor()
                ));
            }
        }
    }*/
}