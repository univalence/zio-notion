// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

const repoUrl = "https://github.com/univalence/zio-notion";

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'ZIO Notion',
  tagline: 'A strongly typed interface to interact with Notion using ZIO',
  url: 'https://univalence.github.io/',
  baseUrl: '/zio-notion/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.ico',

  // GitHub pages deployment config.
  // If you aren't using GitHub pages, you don't need these.
  organizationName: 'univalence', // Usually your GitHub org/user name.
  projectName: 'zio-notion', // Usually your repo name.

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },


  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          routeBasePath: '/',
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: `${repoUrl}/edit/master/docs/`,
          path: '../docs',
        },
        blog: false,
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'ZIO for Notion',
        items: [
          {
            href: repoUrl,
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Documentation',
            items: [
              {
                label: 'Introduction',
                to: '/',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'ZIO',
                href: 'https://zio.dev/',
              },
              {
                label: 'Notion API',
                href: 'https://developers.notion.com/',
              },
            ],
          },
          {
            title: 'Univalence',
            items: [
              {
                label: 'Website',
                href: 'https://univalence.io/',
              },
              {
                label: 'Blog',
                href: 'https://univalence.io/blog/',
              },
              {
                label: 'Github',
                href: 'https://github.com/UNIVALENCE',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} zio-notion, Inc. Built with Docusaurus.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
        additionalLanguages: ['java','scala'],
      },
    }),
};

module.exports = config;
