-- Initialize default OAuth providers
-- Check if providers already exist to avoid duplicates
INSERT INTO oauth_providers (id, name, client_id, client_secret, authorization_uri, token_uri, user_info_uri, redirect_uri, scope, created_at, enabled, created_by)
SELECT 1, 'google', 'google-client-id', 'google-client-secret', 'https://accounts.google.com/o/oauth2/auth', 'https://oauth2.googleapis.com/token', 'https://www.googleapis.com/oauth2/v3/userinfo', 'https://thoth.example.com/auth/oauth/callback/google', 'openid email profile', CURRENT_TIMESTAMP, true, 'system'
WHERE NOT EXISTS (SELECT 1 FROM oauth_providers WHERE name = 'google');

INSERT INTO oauth_providers (id, name, client_id, client_secret, authorization_uri, token_uri, user_info_uri, redirect_uri, scope, created_at, enabled, created_by)
SELECT 2, 'github', 'github-client-id', 'github-client-secret', 'https://github.com/login/oauth/authorize', 'https://github.com/login/oauth/access_token', 'https://api.github.com/user', 'https://thoth.example.com/auth/oauth/callback/github', 'user:email', CURRENT_TIMESTAMP, true, 'system'
WHERE NOT EXISTS (SELECT 1 FROM oauth_providers WHERE name = 'github');

-- Reset sequence if needed
ALTER SEQUENCE oauth_providers_id_seq RESTART WITH 3;