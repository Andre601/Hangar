<script lang="ts" setup>
import { User } from "hangar-api";
import Card from "~/components/design/Card.vue";
import { useI18n } from "vue-i18n";
import ProjectInfo from "~/components/projects/ProjectInfo.vue";
import { HangarProject } from "hangar-internal";
import MemberList from "~/components/projects/MemberList.vue";
import MarkdownEditor from "~/components/MarkdownEditor.vue";
import { hasPerms } from "~/composables/usePerm";
import { NamedPermission } from "~/types/enums";
import { useRoute, useRouter } from "vue-router";
import { useContext } from "vite-ssr/vue";
import Markdown from "~/components/Markdown.vue";
import ProjectPageList from "~/components/projects/ProjectPageList.vue";
import { useProjectPage } from "~/composables/useProjectPage";
import { useHead } from "@vueuse/head";
import { useSeo } from "~/composables/useSeo";
import { projectIconUrl } from "~/composables/useUrlHelper";

const props = defineProps<{
  user: User;
  project: HangarProject;
}>();
const i18n = useI18n();
const route = useRoute();
const context = useContext();
const router = useRouter();
const { editingPage, open, savePage, page } = await useProjectPage(route, router, context, i18n, props.project);

//TODO Save/load in/from project
const sponsors = "It is I, sponsor man";

useHead(useSeo(props.project.name, props.project.description, route, projectIconUrl(props.project.namespace.owner, props.project.namespace.slug)));
</script>

<template>
  <div class="flex flex-wrap md:flex-nowrap gap-4">
    <section class="basis-full md:basis-9/12 flex-grow overflow-auto">
      <Card class="p-0 overflow-clip">
        <MarkdownEditor
          v-if="hasPerms(NamedPermission.EDIT_PAGE)"
          ref="editor"
          v-model:editing="editingPage"
          :raw="page.contents"
          :deletable="false"
          :saveable="true"
          :cancellable="true"
          @save="savePage"
        />
        <Markdown v-else :raw="page.contents" />
      </Card>
      <Card v-if="sponsors" class="mt-2 pl-3 p-0 overflow-clip">
        <h1 class="mt-5 ml-5 text-xl">{{ i18n.t("project.sponsors") }}</h1>
        <!-- todo -->
        <!--<MarkdownEditor
          v-if="hasPerms(NamedPermission.EDIT_PAGE)"
          ref="editor"
          v-model:editing="editingPage"
          :raw="sponsors"
          :deletable="false"
          :saveable="true"
          :cancellable="true"
          :maxlength="500"
          @save="savePage"
        />-->
        <Markdown v-if="sponsors?.length !== 0" :raw="sponsors"
      /></Card>
    </section>
    <section class="basis-full md:basis-3/12 space-y-4 min-w-280px">
      <ProjectInfo :project="project"></ProjectInfo>
      <Card>
        <template #header>{{ i18n.t("project.promotedVersions") }}</template>
        <!-- todo promoted versions go here -->
        <template #default>Promoted versions go here</template>
      </Card>
      <ProjectPageList :project="project" :open="open" />
      <MemberList :model-value="project.members" />
    </section>
  </div>
</template>
