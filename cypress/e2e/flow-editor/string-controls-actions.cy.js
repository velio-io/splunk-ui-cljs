/// <reference types="cypress" />

describe('testing actions with input string control', () => {
    beforeEach(() => {
        cy.visit('http://localhost:6006/iframe.html?id=flow--simple-flow&viewMode=story')
            .wait(500)

        cy.get('.react-flow__node-action')
            .click()
            .find('[data-test="edit-action"]')
            .click()

        cy.get('.react-flow__node-action')
            .find('button[data-test=select]')
            .click()

        cy.wait(200)
    })

    afterEach(() => {
        cy.get('.react-flow__node-action')
            .find('button[type=submit]')
            .click()

        cy.get('.react-flow__node-action')
            .should('have.text', 'Test')
    })

    it('async-queue! action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('async-queue!')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('some queue name')
    })

    it('coll-percentiles action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-percentiles')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('2.1,4.6')
    })

    it('coll-sort action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-sort')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('some-value')
    })

    it('from-base64 action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('from-base64')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('test-key')
    })

    it('index action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('index')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('test-string')
    })

    it('json-fields action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('json-fields')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('first, [second, third]')
    })

    it('keep-keys action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('keep-keys')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('first, [second, third]')
    })

    it('output! action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('output!')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('first, [second, third]')
    })

    it('publish! action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('publish!')
            .click()


        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('some-channel')
    })

    it('sdissoc action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('sdissoc')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('some-key')
    })

    it('tag action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('tag')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('some-tag')
    })

    it('tagged-all action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('tagged-all')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('some-tag')
    })

    it('tap action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('tap')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('foobar')
    })

    it('to-base64 action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('to-base64')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('foo-key')
    })

    it('untag action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('untag')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('some-tag')
    })

    it('reinject! action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('reinject!')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="string-input"] input')
            .type('some-stream')
    })
})
